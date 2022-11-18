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
        public ManualResetEvent ConnectDone { get; set; } = new ManualResetEvent(false);
        public ManualResetEvent SendDone { get; set; } = new ManualResetEvent(false);
        public ManualResetEvent ReceiveDone { get; set; } = new ManualResetEvent(false);
        public Socket Socket { get; set; }
        public static int BufferSize { get; set; } = 1024 * 10;
        public byte[] Buffer { get; set; } = new byte[BufferSize];
        public string Response { get; set; }
    }
}
