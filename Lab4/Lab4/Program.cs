using System;
using System.Linq;

namespace Lab4
{
    class Program
    {
        static void Main(string[] args)
        {
            //var hosts = new[] { "www.cs.ubbcluj.ro/~rlupsa/edu/pdp/progs/futures-demo2-cascade1.cs", "www.cs.ubbcluj.ro/~motogna/LFTC" }.ToList();
            var hosts = new[] { "www.cs.ubbcluj.ro/~rlupsa/edu/pdp/progs/futures-demo2-cascade1.cs" }.ToList();

            CallbackImplementation.Run(hosts);
            //TaskImplementation.Run(hosts);
            //AsyncImplementation.Run(hosts);
        }
    }
}
