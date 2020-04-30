// ThreadDemo.java
class ThreadDemo
{
   public static void main (String [] args)
   {
      MyThread mt;
      //create a new thread
      if (args.length == 0)
          mt = new MyThread ();
      else
          mt = new MyThread (args [0]);

      mt.start (); //start thread : memory setup and initialization code in JVM - platform dependnent 

      //main thread
      for (int i = 0; i < 50; i++)
           System.out.println ("i = " + i + ", i * i = " + i * i);
   }
}
class MyThread extends Thread
{
  MyThread ()
   {
      // The compiler creates the byte code equivalent of super ();
   }
   MyThread (String name)
   {
      super (name); // Pass name to Thread superclass
   }
   public void run ()
   {
      System.out.println ("My name is: " + getName ());
      for (int count = 1, row = 1; row < 20; row++, count++)
      {
           for (int i = 0; i < count; i++)
                System.out.print ('*');
           System.out.print ('\n');
      }
   }
}
