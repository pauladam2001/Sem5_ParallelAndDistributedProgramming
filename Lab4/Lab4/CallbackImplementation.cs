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
        public static int Port { get; set; } = 80;  // HTTP port, the default network port used to send and receive unencrypted web pages
        private static int remainingThreads;
        static object RemainingThreadsLock = new object();

        public static void Run(List<string> hostNames)
        {
            var id = 1;
            remainingThreads = hostNames.Count;
            hostNames.ForEach(hostName =>
            {
                StartClient(hostName, id);
                id += 1;
            });
            lock (RemainingThreadsLock)
            {
                while (remainingThreads > 0)
                {
                    Monitor.Wait(RemainingThreadsLock);     // releases the lock on an object and blocks the current thread until it reacquires the lock
                }
            }
        }

        private static IPEndPoint CreateEndPoint(IPAddress ipAddress) => new IPEndPoint(ipAddress, Port);

        private static IPAddress GetHostIpAddress(string hostName) =>
            // resolves an IP address
            Dns.GetHostEntry(hostName.Split('/')[0]).AddressList[0];

        private static void StartClient(string hostName, int id)
        {
            // establish the remote endpoint of the server
            var ipAddress = GetHostIpAddress(hostName);
            var endPoint = CreateEndPoint(ipAddress);

            // create the socket
            var clientSocket = new Socket(ipAddress.AddressFamily, SocketType.Stream, ProtocolType.Tcp);

            // create a connection containing the connection information
            var connection = new Connection
            {
                Socket = clientSocket,
                HostName = hostName.Split('/')[0],
                EndPoint = hostName.Contains("/") ? hostName.Substring(hostName.IndexOf("/", StringComparison.Ordinal)) : "/",
                IpEndPoint = endPoint,
                Id = id
            };

            // connect to the remote endpoint
            // begins an asynchronous request for a remote host connection
            // param1 - an EndPoint that represents the remote host
            // param2 - the AsyncCallback (references a method to be called when a corresponding asynchronous operation completes)
            // param3 - an object that contains state information for this request
            // returns - an IAsyncResult that references the asynchronous connection
            connection.Socket.BeginConnect(connection.IpEndPoint, Connected, connection);
        }

        private static void Connected(IAsyncResult result)      // IAsyncResult - represents the status of an asynchronous operation
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
            Console.WriteLine($"Connection {clientId} > Socket connected to {hostName} ({clientSocket.RemoteEndPoint})");

            // convert the string into bytes
            var byteData = Encoding.ASCII.GetBytes(HTTPProtocolParser.GetRequestString(resultSocket.HostName, resultSocket.EndPoint));

            // begin sending the data to the server
            // sends data asynchronously to a connected Socket
            // param1 - an array of type Byte that contains the data to send
            // param2 - flags
            // param3 - length of param1
            // param4 - flags
            // param5 - the AsyncCallback (references a method to be called when a corresponding asynchronous operation completes)
            // param6 - an object that contains state information for this request
            // returns - an IAsyncResult that references the asynchronous send
            resultSocket.Socket.BeginSend(byteData, 0, byteData.Length, 0, Sent, resultSocket);
        }

        private static void Sent(IAsyncResult result)       // IAsyncResult - represents the status of an asynchronous operation
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
            Console.WriteLine($"Connection {clientId} > Sent {bytesSent} bytes to server.");

            // begin receiving the data from the server
            // begins to asynchronously receive data from a connected Socket
            // param1 - an array of type Byte that is the storage location for the received data
            // param2 - the position in the buffer parameter at which to store the received data
            // param3 - the number of bytes to receive
            // param4 - flags
            // param5 - an AsyncCallback (references a method to be called when a corresponding asynchronous operation completes)
            // param6 - a user-defined object that contains information about the receive operation. This object is passed to the EndReceive(IAsyncResult) delegate when the operation is complete
            clientSocket.BeginReceive(resultSocket.Buffer, 0, Connection.BufferSize, 0, Receiving, resultSocket);
        }

        private static void Receiving(IAsyncResult result)      // IAsyncResult - represents the status of an asynchronous operation
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
                resultSocket.Response.Append(Encoding.ASCII.GetString(resultSocket.Buffer, 0, bytesRead));
                // Console.WriteLine(Encoding.ASCII.GetString(resultSocket.Buffer, 0, bytesRead));

                // if the response header is not fully obtained we read the next data
                if (!HTTPProtocolParser.ResponseHeaderObtained(resultSocket.Response.ToString()))
                {
                    clientSocket.BeginReceive(resultSocket.Buffer, 0, Connection.BufferSize, 0, Receiving, resultSocket);
                } 
                else   // the response header is fully obtained
                {
                    var responseBody = HTTPProtocolParser.GetResponseBody(resultSocket.Response.ToString());

                    // print the content length and the length of the received data
                    var contentLength = HTTPProtocolParser.GetContentLength(resultSocket.Response.ToString());
                    Console.WriteLine($"Content length is: {contentLength}");
                    Console.WriteLine($"Response length is: {responseBody.Length}");
                    Console.WriteLine();

                    // if we still haven't received the full response
                    if (responseBody.Length < contentLength)
                    {
                        clientSocket.BeginReceive(resultSocket.Buffer, 0, Connection.BufferSize, 0, Receiving, resultSocket);
                    }
                    else    // we received the full response
                    {
                        // print the received data
                        Console.WriteLine(resultSocket.Response);

                        // release and close the socket
                        clientSocket.Shutdown(SocketShutdown.Both);     // disables sends and receives on clientSocket
                        clientSocket.Close();                           // closes clientSocket connection and releases all resources

                        lock (RemainingThreadsLock)
                        {
                            remainingThreads--;
                            Monitor.Pulse(RemainingThreadsLock);    // notifies a thread in the waiting queue of a change in the locked object's state
                        }
                    }
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }
        }
    }
}
