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
            return "GET " + endPoint + " HTTP/1.1\r\n" + "Host: " + hostName + "\r\n" + "Content-Length: 0\r\n\r\n";
        }

        public static int GetContentLength(string response)
        {
            var contentLength = 0;
            var responseLines = response.Split('\r', '\n').ToList();

            responseLines.ForEach(responseLine =>
            {
                var headDetails = responseLine.Split(':').ToList();
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
