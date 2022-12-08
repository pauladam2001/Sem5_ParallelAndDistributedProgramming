using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Lab4
{
    public class HTTPProtocolParser
    {
        public static string GetRequestString(string hostName, string endPoint)
        {
            // hostname = www.cs.ubbcluj.ro
            // endpoint = /~rlupsa/edu/pdp/progs/futures-demo2-cascade1.cs
            return "GET " + endPoint + " HTTP/1.1\r\n" + "Host: " + hostName + "\r\n" + "Content-Length: 0\r\n\r\n";    // HTTP 1.1 is the latest version of HTTP
        }

        public static int GetContentLength(string response)
        {
            var contentLength = 0;
            var responseLines = response.Split('\r', '\n').ToList();

            // one of the response lines will have the form: "Content-Length: X"
            responseLines.ForEach(responseLine =>
            {
                var headDetails = responseLine.Split(':').ToList();

                // headDetails[0] = "Content-Length"
                // headDetails[1] = actual length
                if (string.Compare(headDetails[0], "Content-Length", StringComparison.Ordinal) == 0)
                {
                    contentLength = int.Parse(headDetails[1]);
                }
            });

            return contentLength;
        }

        public static bool ResponseHeaderObtained(string response)
        {
            return response.Contains("\r\n\r\n");
        }
    }
}
