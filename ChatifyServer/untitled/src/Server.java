import javax.swing.plaf.basic.BasicComboBoxUI;
import java.io.*;
import java.net.*;
import java.util.*;


public class Server {
    static Map<String, Socket> map = new HashMap<String, Socket>();

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

    public static void main(String[] args) throws IOException{
        try{
            ServerSocket server = new ServerSocket(8080);


            CommandHandler();

            while(true){
                CheckConnection();
                Socket client = server.accept();

                Thread t = new Thread(() -> {
                    try{
                        var in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        var out = new PrintWriter(client.getOutputStream(), true);

                        var nick = in.readLine();

                        if(map.get(nick) == null){
                            map.put(nick, client);
                            String input;
                            do{
                                System.out.println(in.read());
                                input = in.readLine();
                                if(input == null){
                                    client.close();
                                    map.remove(client);
                                    break;
                                }
                                out.println(nick + ": " + input);
                            }while(input != null);


                        }else{
                            out.println("Nickname is already in use");
                        }


                    }catch(Exception e){
                        System.out.println(e.getMessage());
                    }
                });

                t.start();

            }


        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}