package Server;

import java.io.File;
import java.io.FileWriter;
import java.util.Random;

public class TextFileGenerator {
    public static void main(String[] args) {
        String[] first_name = { "Nguyen", "Tran", "Ngo", "Pham", "Dao", "Le", "Do", "Mai", "Dinh", "Ly", "Cu", "Vuong",
                "Cao", "Phi" };
        String[] mid_name = { "Ngoc", "Huyen", "Trang", "Thi", "Van", "Dai", "Hoang", "Anh", "Duc", "Khanh", "Nhat",
                "Minh", "Quang", "Hong", "Thien", "Tien" };
        String[] last_name = { "Nhanh", "Nghia", "Son", "Manh", "Thanh", "Trang", "Thao", "Tien", "Vui", "Ve", "Mai",
                "Hung", "Anh", "Nhi", "Thu", "Tham", "Nu", "Tuyet" };
        FileWriter pw;

        try {
            for (int j = 0; j < 20; j++) {
                pw = new FileWriter(new File("TestPayload.txt"));
                StringBuilder sb = new StringBuilder();
                if(j == 1){
                    sb.append("student_id");
                    sb.append(",");
                    sb.append("student_name");
                    sb.append(",");
                    sb.append("student_age");
                    sb.append(",");
                    sb.append("class_id");
                    sb.append("\r\n");
                }

                for (int i = 0; i < 1000000; i++) {
                    Random generator = new Random();
                    int randomIndex1 = generator.nextInt(first_name.length);
                    int randomIndex2 = generator.nextInt(mid_name.length);
                    int randomIndex3 = generator.nextInt(last_name.length);
                    String name = first_name[randomIndex1].concat(" ").concat(mid_name[randomIndex2]).concat(" ")
                            .concat(last_name[randomIndex3]);
                    int age = (int) (Math.random() * 30 + 10);
                    int student_id = i + j*1000000 + 1 ;
                    int class_id = (int) (Math.random() * 1000000 + 1);
                    sb.append(student_id);
                    sb.append(",");
                    sb.append(name);
                    sb.append(",");
                    sb.append(age);
                    sb.append(",");
                    sb.append(class_id);
                    sb.append("\r\n");
                }
                pw.write(sb.toString());
                pw.flush();
                pw.close();
                System.out.println("finished "+j);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}