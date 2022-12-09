using System;
using System.Linq;

namespace Lab4
{
    class Program
    {
        static void Main(string[] args)
        {
            var hosts = new[] { "www.cs.ubbcluj.ro/~hfpop/teaching/pfl/requirements.html" }.ToList();
            //var hosts = new[] { "www.cs.ubbcluj.ro/~motogna/LFTC", "www.cs.ubbcluj.ro/~hfpop/teaching/pfl/requirements.html" }.ToList();

            CallbackImplementation.Run(hosts);
            //TaskImplementation.Run(hosts);
            //AsyncImplementation.Run(hosts);
        }
    }
}
