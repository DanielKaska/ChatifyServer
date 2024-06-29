import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.synth.SynthRadioButtonMenuItemUI;
import java.io.*;
import java.net.*;
import java.util.*;


public class Server {
    static Map<String, Socket> map = new HashMap<String, Socket>();

    static ServerSocket server;

    static void CommandHandler(){
        Thread command = new Thread(() -> {
            while (true){
                Scanner scanner = new Scanner(System.in);
                var input = scanner.nextLine();

                if(input.equals("users")){
                    for(String key : map.keySet()){
                        System.out.print(key + " ");
                    }
                }

            }
        });
        command.start();
    }

    static void CheckConnection() throws IOException {
        for(var s : map.keySet()){
            var client = map.get(s);

            try{
                client.getOutputStream().write(1);
            }catch(Exception e){
                System.out.println("Client " + s + " closed");
                client.close();
                map.remove(s);
            }

        }
    }

    static void SendMessages(String message) throws IOException {
        for(var nick : map.keySet()){
            var client = map.get(nick);
            var writer = new PrintWriter(client.getOutputStream(), true);
            writer.println(message);
        }
    }

    private static volatile String input;

    static boolean IsNickAvailable(String nick){
        if(map.get(nick) == null)
            return true;

        return false;
    }

    static String GetUsersAsString(){
        String res = "";
        for(var c : map.keySet()){
            res += c;
        }

        return res;
    }

    static String AcceptClients() throws IOException {
        Socket client = server.accept();
        Thread t = new Thread(() -> {
            try {

                var reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                var nick = reader.readLine();

                if(IsNickAvailable(nick)) {
                    map.put(nick, client);
                    SendMessages(nick + " joined the chat");
                    do{
                        try {
                            input = reader.readLine();
                            SendMessages(nick + ": " + input);

                        } catch (Exception e) {
                        }
                    }while(input != null);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        });

        t.start();
        return "";
    }

    public static void main(String[] args) throws IOException{
        try{
            server = new ServerSocket(8080);
            CommandHandler();

            Thread mainThread = new Thread(() -> {
                while(true){
                    try {
                        AcceptClients();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            });

            Thread thread2 = new Thread(() -> {
                while(true){
                    try {
                        Thread.sleep(5000);
                        CheckConnection();
                        AcceptClients();

                    }catch (Exception e) {System.out.println(e.getMessage());}
                }
            });
            thread2.start();
            mainThread.start();


        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}