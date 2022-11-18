using System;
using System.Collections.Generic;
using System.Text;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Threading.Tasks;

namespace Lab4
{
    public class AsyncImplementation
    {
        public static int Port { get; set; } = 80;
        public static List<string> Hosts { get; set; }

        public static void Run(List<string> hostNames)
        {
            var id = 0;
            Hosts = hostNames;
            var tasks = new List<Task>();

            hostNames.ForEach(_ =>
            {
                tasks.Add(Task.Factory.StartNew(Start, id));
                id += 1;
            });

            Task.WaitAll(tasks.ToArray());
        }

        private static IPEndPoint CreateEndPoint(IPAddress ipAddress) => new IPEndPoint(ipAddress, Port);

        private static IPAddress GetHostIpAddress(string hostName) =>
            Dns.GetHostEntry(hostName.Split('/')[0]).AddressList[0];

        private static void Start(object idObject)
        {
            var id = (int)idObject;
            StartClient(Hosts[id], id);
        }

        private static async void StartClient(string hostName, int id)
        {
            var ipAddress = GetHostIpAddress(hostName);
            var endPoint = CreateEndPoint(ipAddress);
            var client = new Socket(ipAddress.AddressFamily, SocketType.Stream, ProtocolType.Tcp);

            var connection = new Connection
            {
                Socket = client,
                HostName = hostName.Split('/')[0],
                EndPoint = hostName.Contains("/") ? hostName.Substring(hostName.IndexOf("/", StringComparison.Ordinal)) : "/",
                IpEndPoint = endPoint,
                Id = id
            };

            await Connect(connection);
            await Send(connection, HTTPProtocolParser.GetRequestString(connection.HostName, connection.EndPoint));
            await Receive(connection);

            Console.WriteLine("Connection {0} > Content length is:{1}", connection.Id, HTTPProtocolParser.GetContentLength(connection.Response.ToString()));

            client.Shutdown(SocketShutdown.Both);
            client.Close();
        }

        private static async Task Connect(Connection state)
        {
            state.Socket.BeginConnect(state.IpEndPoint, ConnectCallback, state);

            await Task.FromResult<object>(state.ConnectDone.WaitOne());
        }

        private static void ConnectCallback(IAsyncResult result)
        {
            var resultSocket = (Connection)result.AsyncState;
            var clientSocket = resultSocket.Socket;
            var clientId = resultSocket.Id;
            var hostName = resultSocket.HostName;

            clientSocket.EndConnect(result);

            Console.WriteLine("Connection {0} > Socket connected to {1} ({2})", clientId, hostName, clientSocket.RemoteEndPoint);

            resultSocket.ConnectDone.Set();
        }

        private static async Task Send(Connection state, string data)
        {
            var byteData = Encoding.ASCII.GetBytes(data);

            state.Socket.BeginSend(byteData, 0, byteData.Length, 0, SendCallback, state);

            await Task.FromResult<object>(state.SendDone.WaitOne());
        }

        private static void SendCallback(IAsyncResult result)
        {
            var resultSocket = (Connection)result.AsyncState;
            var clientSocket = resultSocket.Socket;
            var clientId = resultSocket.Id;

            var bytesSent = clientSocket.EndSend(result);

            Console.WriteLine("Connection {0} > Sent {1} bytes to server.", clientId, bytesSent);

            resultSocket.SendDone.Set();
        }

        private static async Task Receive(Connection connection)
        {
            connection.Socket.BeginReceive(connection.Buffer, 0, Connection.BufferSize, 0, ReceiveCallback, connection);

            await Task.FromResult<object>(connection.ReceiveDone.WaitOne());
        }

        private static void ReceiveCallback(IAsyncResult result)
        {
            var resultSocket = (Connection)result.AsyncState;
            var clientSocket = resultSocket.Socket;

            try
            {
                var bytesRead = clientSocket.EndReceive(result);

                resultSocket.Response += Encoding.ASCII.GetString(resultSocket.Buffer, 0, bytesRead);

                if (!HTTPProtocolParser.ResponseHeaderObtained(resultSocket.Response))
                {
                    clientSocket.BeginReceive(resultSocket.Buffer, 0, Connection.BufferSize, 0, ReceiveCallback, resultSocket);
                }
                else
                {
                    string output = Encoding.Default.GetString(resultSocket.Buffer);
                    Console.WriteLine(output);
                    resultSocket.ReceiveDone.Set();    
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }

        }
    }
}
