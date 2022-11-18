using System;
using System.Collections.Generic;
using System.Text;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Threading.Tasks;

namespace Lab4
{
    public class CallbackImplementation
    {
        public static int Port { get; set; } = 80;

        public static void Run(List<string> hostNames)
        {
            var id = 1;

            hostNames.ForEach(hostName =>
            {
                StartClient(hostName, id);
                Thread.Sleep(1000);
                id += 1;
            });
        }

        private static IPEndPoint CreateEndPoint(IPAddress ipAddress) => new IPEndPoint(ipAddress, Port);

        private static IPAddress GetHostIpAddress(string hostName) =>
            Dns.GetHostEntry(hostName.Split('/')[0]).AddressList[0];

        private static void StartClient(string hostName, int id)
        {
            var ipAddress = GetHostIpAddress(hostName);
            var endPoint = CreateEndPoint(ipAddress);
            var clientSocket = new Socket(ipAddress.AddressFamily, SocketType.Stream, ProtocolType.Tcp);
            var connection = new Connection
            {
                Socket = clientSocket,
                HostName = hostName.Split('/')[0],
                EndPoint = hostName.Contains("/") ? hostName.Substring(hostName.IndexOf("/", StringComparison.Ordinal)) : "/",
                IpEndPoint = endPoint,
                Id = id
            };

            connection.Socket.BeginConnect(connection.IpEndPoint, Connected, connection);
        }

        private static void Connected(IAsyncResult result)
        {
            var resultSocket = (Connection)result.AsyncState;
            var clientSocket = resultSocket.Socket;
            var clientId = resultSocket.Id;
            var hostName = resultSocket.HostName;

            clientSocket.EndConnect(result);
            Console.WriteLine($"Connection {clientId} > Socket connected to {hostName} ({clientSocket.RemoteEndPoint})");

            var byteData = Encoding.ASCII.GetBytes(HTTPProtocolParser.GetRequestString(resultSocket.HostName, resultSocket.EndPoint));

            resultSocket.Socket.BeginSend(byteData, 0, byteData.Length, 0, Sent, resultSocket);
        }

        private static void Sent(IAsyncResult result)
        {
            var resultSocket = (Connection)result.AsyncState;
            var clientSocket = resultSocket.Socket;
            var clientId = resultSocket.Id;

            var bytesSent = clientSocket.EndSend(result);
            Console.WriteLine($"Connection {clientId} > Sent {bytesSent} bytes to server.");

            resultSocket.Socket.BeginReceive(resultSocket.Buffer, 0, Connection.BufferSize, 0, Receiving, resultSocket);
        }

        private static void Receiving(IAsyncResult result)
        {
            var resultSocket = (Connection)result.AsyncState;
            var clientSocket = resultSocket.Socket;

            try
            {
                var bytesRead = clientSocket.EndReceive(result);
                resultSocket.Response += Encoding.ASCII.GetString(resultSocket.Buffer, 0, bytesRead);

                if (!HTTPProtocolParser.ResponseHeaderObtained(resultSocket.Response))
                {
                    clientSocket.BeginReceive(resultSocket.Buffer, 0, Connection.BufferSize, 0, Receiving, resultSocket);
                } 
                else
                {
                    var contentLength = HTTPProtocolParser.GetContentLength(resultSocket.Response);
                    Console.WriteLine($"Content length is: {contentLength}");
                    
                    var output = Encoding.Default.GetString(resultSocket.Buffer);
                    Console.WriteLine(output);

                    clientSocket.Shutdown(SocketShutdown.Both);
                    clientSocket.Close();
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }
        }
    }
}
