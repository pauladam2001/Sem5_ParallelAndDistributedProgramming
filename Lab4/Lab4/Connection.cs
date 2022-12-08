using System;
using System.Collections.Generic;
using System.Text;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Threading.Tasks;

namespace Lab4
{
    public class Connection
    {
        public int Id { get; set; }
        public string HostName { get; set; }
        public string EndPoint { get; set; }
        public IPEndPoint IpEndPoint { get; set; }
        public ManualResetEvent ConnectDone { get; set; } = new ManualResetEvent(false);    // ManualResetEvent - represents a thread synchronization event that, when signaled, must be reset manually
        public ManualResetEvent SendDone { get; set; } = new ManualResetEvent(false);       // ManualResetEvent - represents a thread synchronization event that, when signaled, must be reset manually
        public ManualResetEvent ReceiveDone { get; set; } = new ManualResetEvent(false);    // ManualResetEvent - represents a thread synchronization event that, when signaled, must be reset manually
        public Socket Socket { get; set; }                                                  // ManualResetEvent is used for sending signals between 2 or more threads
        public static int BufferSize { get; set; } = 1024 * 10;
        public byte[] Buffer { get; set; } = new byte[BufferSize];
        public string Response { get; set; }
    }
}
