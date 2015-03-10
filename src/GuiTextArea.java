import javax.swing.*;        

public class GuiTextArea {
    
    JTextArea myArea;

    //--------------------
    GuiTextArea(String title) {
	
	//Create and set up the window
	JFrame frame = new JFrame(title);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	myArea = new JTextArea(20, 40);
	myArea.setEditable(false);
	JScrollPane scrollPane = 
	    new JScrollPane(myArea,
			    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			    JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	
	frame.getContentPane().add(scrollPane);
	
	//Display the window.
	frame.pack();
	frame.setVisible(true);
    }

    //--------------------
    public void print(String s)   { 
	myArea.append(s); 
        myArea.setCaretPosition(myArea.getDocument().getLength());
    }
    public void println(String s) { print(s+"\n"); }
    public void println()         { print("\n"); }

    final static String SPACES = "                                                ";
      public static String format(String s, int len){
  	int slen = len-s.length();

  	if(slen > SPACES.length())
  	    slen = SPACES.length();
    
  	if(slen > 0)
  	    return SPACES.substring(0,slen)+s;
  	else
  	    return s;

      }

      public static String format(Object x, int len){
  	return format(String.valueOf(x), len);
      }

      public static String format(long x, int len){
  	return format(String.valueOf(x), len);
      }

      public static String format(double x, int len){
  	return format(String.valueOf(x), len);
      }

      public static String format(char x, int len){
  	return format(String.valueOf(x), len);
      }

}
