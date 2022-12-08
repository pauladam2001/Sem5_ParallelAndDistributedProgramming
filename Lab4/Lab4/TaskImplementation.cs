using System;
using System.Collections.Generic;
using System.Text;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Threading.Tasks;

namespace Lab4
{
    public class TaskImplementation
    {
        public static int Port { get; set; } = 80;  // HTTP port, the default network port used to send and receive unencrypted web pages
        public static List<string> Hosts { get; set; }

        public static void Run(List<string> hostNames)
        {
            var id = 0;
            Hosts = hostNames;
            var tasks = new List<Task>();

            hostNames.ForEach(_ =>
            {
                // Task.Factory.StartNew - creates and starts a task
                tasks.Add(Task.Factory.StartNew(Start, id));
                id += 1;
            });

            // waits for all of the provided Task objects to complete execution
            // param1 - An array of Task instances on which to wait
            Task.WaitAll(tasks.ToArray());
        }

        private static IPEndPoint CreateEndPoint(IPAddress ipAddress) => new IPEndPoint(ipAddress, Port);

        private static IPAddress GetHostIpAddress(string hostName) =>
            // resolves an IP address
            Dns.GetHostEntry(hostName.Split('/')[0]).AddressList[0];

        private static void Start(object idObject)
        {
            var id = (int)idObject;
            StartClient(Hosts[id], id);
        }

        private static void StartClient(string hostName, int id)
        {
            // establish the remote endpoint of the server
            var ipAddress = GetHostIpAddress(hostName);
            var endPoint = CreateEndPoint(ipAddress);

            // create the TCP socket
            var client = new Socket(ipAddress.AddressFamily, SocketType.Stream, ProtocolType.Tcp);

            // create a connection containing the connection information
            var requestSocket = new Connection
            {
                Socket = client,
                HostName = hostName.Split('/')[0],
                EndPoint = hostName.Contains("/") ? hostName.Substring(hostName.IndexOf("/", StringComparison.Ordinal)) : "/",
                IpEndPoint = endPoint,
                Id = id
            };

            // connect to the remote endpoint
            // waits for the Task to complete execution
            Connect(requestSocket).Wait();

            // request data from the server
            // waits for the Task to complete execution
            Send(requestSocket, HTTPProtocolParser.GetRequestString(requestSocket.HostName, requestSocket.EndPoint)).Wait();

            // receive the response from the server
            // waits for the Task to complete execution
            Receive(requestSocket).Wait();

            Console.WriteLine("Connection {0} > Content length is:{1}", requestSocket.Id, HTTPProtocolParser.GetContentLength(requestSocket.Response.ToString()));

            // release and close the socket
            client.Shutdown(SocketShutdown.Both);   // disables sends and receives on clientSocket
            client.Close();                         // closes clientSocket connection and releases all resources
        }

        private static Task Connect(Connection state)
        {
            // connect to the remote endpoint
            // begins an asynchronous request for a remote host connection
            // param1 - an EndPoint that represents the remote host
            // param2 - the AsyncCallback (references a method to be called when a corresponding asynchronous operation completes)
            // param3 - an object that contains state information for this request
            // returns - an IAsyncResult that references the asynchronous connection
            state.Socket.BeginConnect(state.IpEndPoint, ConnectCallback, state);

            // ManualResetEvent.WaitOne - blocks the current thread until the current WaitHandle (ConnectDone) receives a signal.
            // Task.FromResult - returns the sucessfully completed task
            return Task.FromResult(state.ConnectDone.WaitOne());
        }

        private static void ConnectCallback(IAsyncResult result)       // IAsyncResult - represents the status of an asynchronous operation
        {
            // get the details
            var resultSocket = (Connection)result.AsyncState;
            var clientSocket = resultSocket.Socket;
            var clientId = resultSocket.Id;
            var hostName = resultSocket.HostName;

            // complete the connection
            // ends a pending asynchronous connection request
            // param1 - an IAsyncResult that stores state information and any user defined data for this asynchronous operation
            clientSocket.EndConnect(result);
            Console.WriteLine("Connection {0} > Socket connected to {1} ({2})", clientId, hostName, clientSocket.RemoteEndPoint);

            // signal that the connection has been made
            // ManualResetEvent.Set - sending the signal to all waiting threads
            resultSocket.ConnectDone.Set();
        }

        private static Task Send(Connection connection, string data)
        {
            // convert string into bytes
            var byteData = Encoding.ASCII.GetBytes(data);

            // begin sending the data to the server
            // sends data asynchronously to a connected Socket
            // param1 - an array of type Byte that contains the data to send
            // param2 - flags
            // param3 - length of param1
            // param4 - flags
            // param5 - the AsyncCallback (references a method to be called when a corresponding asynchronous operation completes)
            // param6 - an object that contains state information for this request
            // returns - an IAsyncResult that references the asynchronous send
            connection.Socket.BeginSend(byteData, 0, byteData.Length, 0, SendCallback, connection);


            // ManualResetEvent.WaitOne - blocks the current thread until the current WaitHandle (SendDone) receives a signal.
            // Task.FromResult - returns the sucessfully completed task
            return Task.FromResult(connection.SendDone.WaitOne());
        }

        private static void SendCallback(IAsyncResult result)       // IAsyncResult - represents the status of an asynchronous operation
        {
            // get the details
            var resultSocket = (Connection)result.AsyncState;
            var clientSocket = resultSocket.Socket;
            var clientId = resultSocket.Id;

            // complete sending the data to the server
            // ends a pending asynchronous send
            // param1 - an IAsyncResult that stores state information for this asynchronous operation
            // returns - the number of bytes sent to the Socket
            var bytesSent = clientSocket.EndSend(result);
            Console.WriteLine("Connection {0} > Sent {1} bytes to server.", clientId, bytesSent);

            // signal that all bytes have been sent
            // ManualResetEvent.Set - sending the signal to all waiting threads
            resultSocket.SendDone.Set();
        }

        private static Task Receive(Connection connection)
        {
            // begin receiving the data from the server
            // begins to asynchronously receive data from a connected Socket
            // param1 - an array of type Byte that is the storage location for the received data
            // param2 - the position in the buffer parameter at which to store the received data
            // param3 - the number of bytes to receive
            // param4 - flags
            // param5 - an AsyncCallback (references a method to be called when a corresponding asynchronous operation completes)
            // param6 - a user-defined object that contains information about the receive operation. This object is passed to the EndReceive(IAsyncResult) delegate when the operation is complete
            connection.Socket.BeginReceive(connection.Buffer, 0, Connection.BufferSize, 0, ReceiveCallback, connection);

            // ManualResetEvent.WaitOne - blocks the current thread until the current WaitHandle (ReceiveDone) receives a signal.
            // Task.FromResult - returns the sucessfully completed task
            return Task.FromResult(connection.ReceiveDone.WaitOne());
        }

        private static void ReceiveCallback(IAsyncResult result)       // IAsyncResult - represents the status of an asynchronous operation
        {
            // get the details
            var resultSocket = (Connection)result.AsyncState;
            var clientSocket = resultSocket.Socket;

            try
            {
                // read the data from the server
                // ends a pending asynchronous read
                // param1 - an IAsyncResult that stores state information and any user defined data for this asynchronous operation
                // returns - the number of bytes received
                var bytesRead = clientSocket.EndReceive(result);

                // get a number of chars <= buffer size and store it in Reponse
                resultSocket.Response += Encoding.ASCII.GetString(resultSocket.Buffer, 0, bytesRead);

                string output = Encoding.Default.GetString(resultSocket.Buffer);
                Console.WriteLine(output);

                // if the response header is not fully obtained we read the next data
                if (!HTTPProtocolParser.ResponseHeaderObtained(resultSocket.Response))
                {
                    clientSocket.BeginReceive(resultSocket.Buffer, 0, Connection.BufferSize, 0, ReceiveCallback, resultSocket);
                }
                else   // the response header is fully obtained
                {
                    // signal that all bytes have been received
                    // ManualResetEvent.Set - sending the signal to all waiting threads
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
