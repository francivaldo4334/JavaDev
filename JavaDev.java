import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;

public class JavaDev {
    public static void main(String[] args) throws IOException, InterruptedException {
        String pathString = new File(".").getCanonicalPath();
        if (args.length == 0) {
            help("");
            return;
        }
        switch (args[0]) {
            case "new": {
                switch (args[1]) {
                    case "console": {
                        String pkg = "";
                        Boolean ok = true;
                        if (args.length > 1)
                            for (int i = 0; i < args.length; i++) {
                                switch (args[i]) {
                                    case "-r": {
                                        if (args.length > i + 1) {
                                            String res = insertResource(args[i + 1], pathString);
                                            if (res == "*") {
                                                help("console -r");
                                                ok = false;
                                                break;
                                            }
                                            pathString = res;
                                        } else {
                                            help("console -r");
                                            ok = false;
                                        }
                                        break;
                                    }
                                    case "-p": {
                                        if (args.length > i + 1) {
                                            pkg = args[i + 1];
                                            if (!validPkg(pkg)) {
                                                help("console -p");
                                                ok = false;
                                                break;
                                            }
                                        } else {
                                            help("console -p");
                                            ok = false;
                                        }
                                        break;
                                    }
                                }
                            }

                        if (ok) {
                            if (!createPath(pathString))
                                break;
                            createConsole(pathString, pkg);
                        }
                        break;
                    }
                    case "app": {
                        String pkg = "";
                        Boolean ok = true;
                        if (args.length > 1)
                            for (int i = 0; i < args.length; i++) {
                                switch (args[i]) {
                                    case "-r": {
                                        if (args.length > i + 1) {
                                            String res = insertResource(args[i + 1], pathString);
                                            if (res == "*") {
                                                help("console -r");
                                                ok = false;
                                                break;
                                            }
                                            pathString = res;
                                        } else {
                                            help("console -r");
                                            ok = false;
                                        }
                                        break;
                                    }
                                    case "-p": {
                                        if (args.length > i + 1) {
                                            pkg = args[i + 1];
                                            if (!validPkg(pkg)) {
                                                help("console -p");
                                                ok = false;
                                                break;
                                            }
                                        } else {
                                            help("console -p");
                                            ok = false;
                                        }
                                        break;
                                    }
                                }
                            }

                        if (ok) {
                            if (!createPath(pathString))
                                break;
                            createApp(pathString, pkg);
                        }

                        break;
                    }
                    default: {
                        help("new");
                        break;
                    }
                }
                break;
            }
            case "run": {
                if (args.length > 1)
                    if (args[1].equalsIgnoreCase("-r") && args.length > 2) {
                        String res = insertResource(args[2], pathString);
                        if (res == "*") {
                            help("console -r");
                            break;
                        }
                        pathString = res;
                    } else {
                        help("console -r");
                        break;
                    }
                debugProject(pathString);
                break;
            }
            case "-h", "--help": {
                if (args.length > 1 && args[1] == "new")
                    help("new");
                else
                    help("");
                break;
            }
            default: {
                if (args.length > 1 && args[1] == "new")
                    help("new");
                else
                    help("");
                break;
            }
        }
    }

    public static void debugProject(String pathString) throws IOException, InterruptedException {
        File myObj = new File(pathString + "/conf.jvt");
        if (!myObj.exists()) {
            System.out.println("\"conf.jvt\" file not found.");
            return;
        }
        Path path = Paths.get(pathString);
        String fileName = path.getFileName().toString();
        try (Scanner myReader = new Scanner(myObj)) {
            String path1 = "";
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (contains("package:", data.toCharArray()))
                    path1 = getPkgToPath(
                            subString(8, data.toCharArray().length, data.toCharArray()));
                else if (contains("main_class:", data.toCharArray()))
                    fileName = subString(11, data.toCharArray().length, data.toCharArray());
            }
            exec(new String[] { "javac", "-d", pathString + "/" + "bin",
                    pathString + "/" + path1 + fileName + ".java" });
        }
        exec(new String[] { "java", "-cp", pathString + "/bin/", fileName });
    }

    public static Boolean validPkg(String pkg) {
        char[] valids = {
                'a',
                'b',
                'c',
                'd',
                'e',
                'f',
                'g',
                'h',
                'i',
                'j',
                'k',
                'l',
                'm',
                'n',
                'o',
                'p',
                'q',
                'r',
                's',
                't',
                'x',
                'w',
                'y',
                'z',
                'u',
                'v',
                '.' };
        char[] check = pkg.toCharArray();
        if (check[0] == '.' || check[check.length - 1] == '.')
            return false;
        for (char c : check) {
            int i = 0;
            for (char d : valids) {
                if (c == d)
                    break;
                i++;
                if (i == valids.length)
                    return false;
            }
        }
        return true;
    }

    public static void createConsole(String pathString, String pkg) throws IOException {
        Path path = Paths.get(pathString);
        String pathString1 = getPkgToPath(pkg);
        String localPath = pathString + "/" + pathString1;
        createPath(localPath);
        createFile(localPath + "/", path.getFileName().toString());
        createFileConfJson(pathString + "/", "", "1.0", path.getFileName().toString(), "console");
        createPath(pathString + "/bin");
    }

    public static void createApp(String pathString, String pkg) throws IOException {
        String pathString1 = getPkgToPath(pkg);
        String localPath = pathString + "/" + pathString1;
        createPath(localPath);
        createFileApp(localPath + "/", "Main");
        createFileConfJson(pathString + "/", pkg, "1.0", "Main", "app");
        createPath(pathString + "/bin");
    }

    public static String insertResource(String arg1, String pathString) {
        String p1 = arg1;
        char[] cp1 = p1.toCharArray();
        if (cp1[0] == '.' && cp1[1] == '/')
            pathString += subString(1, cp1.length, cp1);
        else if (cp1[0] == '.')
            pathString += "/" + subString(1, cp1.length, cp1);
        else if (cp1[0] == '/')
            pathString += p1;
        else if (((cp1[0] == 'C' || cp1[0] == 'c') && cp1[1] == ':') || (cp1[0] == '~' && cp1[1] == '/'))
            pathString = p1;
        else
            pathString += "/" + p1;
        return pathString;
    }

    public static String getPkgToPath(String pkg) {
        char[] pkgArray = pkg.toCharArray();
        char[] res = new char[pkgArray.length];
        for (int i = 0; i < pkgArray.length; i++) {
            if (pkgArray[i] == '.')
                res[i] = '/';
            else
                res[i] = pkgArray[i];
        }
        return String.valueOf(res) + "/";
    }

    public static String subString(int it0, int it1, char[] it2) {
        int size = it1 - it0;
        char[] res = new char[size];
        for (int i = it0; i < it1; i++) {
            res[i - it0] = it2[i];
        }
        return String.valueOf(res);
    }

    public static Boolean contains(String it0, char[] it1) {
        char[] contain = it0.toCharArray();
        if (contain.length > it1.length)
            return false;
        int index = 0;
        for (int i = 0; i < it1.length; i++) {
            if (i < contain.length && it1[i] == contain[i]) {
                index++;
                if (index == contain.length)
                    break;
            } else
                index = 0;
        }
        if (index == contain.length)
            return true;
        return false;
    }

    public static void help(String it) {
        switch (it) {
            case "new": {
                System.out.println(
                        "options:\n" +
                                "   console                 Console aplication.\n" +
                                "   app <package>           Interface aplication.");
                break;
            }
            case "console -r": {
                System.out.println(
                        "options:\n" +
                                "   -r <path>              Set new path to project.");
                break;
            }
            case "console -p": {
                System.out.println(
                        "options:\n" +
                                "   -p <package>            Set package in aplication.");
                break;
            }
            default: {
                System.out.println(
                        "commands:\n" +
                                "   new <options> [params]  New project.\n" +
                                "   run                     Debug project.\n" +
                                "   -h|--help [params]      List commands.");
                break;
            }
        }
    }

    public static void exec(String[] args) throws IOException, InterruptedException {
        Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec(args);
        pr.waitFor();
        BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String line = "";
        while ((line = buf.readLine()) != null) {
            System.out.println(line);
        }
    }

    public static void createFile(String path, String name) throws IOException {
        File myObj = new File(path + name + ".java");
        try {
            if (myObj.createNewFile()) {
                System.out.println(name + ".java:" + " Success");
            }
        } catch (IOException e) {
            System.out.println(name + ".java:" + " An error occurred.");
            e.printStackTrace();
        }
        FileOutputStream fos = new FileOutputStream(myObj);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        bw.write("public class " + name + " {");
        bw.newLine();
        bw.write("  public static void main(String[] args){");
        bw.newLine();
        bw.write("    System.out.println(\"Hello world\");");
        bw.newLine();
        bw.write("  }");
        bw.newLine();
        bw.write("}");
        bw.newLine();
        bw.close();

    }

    public static void createFileApp(String path, String name) throws IOException {
        File myObj = new File(path + name + ".java");
        try {
            if (myObj.createNewFile()) {
                System.out.println(name + ".java:" + " Success");
            }
        } catch (IOException e) {
            System.out.println(name + ".java:" + " An error occurred.");
            e.printStackTrace();
        }
        FileOutputStream fos = new FileOutputStream(myObj);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        bw.write("import javax.swing.JFrame;");
        bw.newLine();
        bw.write("public class " + name + " {");
        bw.newLine();
        bw.write("  public static void main(String[] args){");
        bw.newLine();
        bw.write("  JFrame win = new JFrame();");
        bw.newLine();
        bw.write("  win.setSize(700,700);");
        bw.newLine();
        bw.write("  win.setTitle(\"Hello world\");");
        bw.newLine();
        bw.write("  win.setVisible(true);");
        bw.newLine();
        bw.write("  }");
        bw.newLine();
        bw.write("}");
        bw.newLine();
        bw.close();

    }

    public static void createFileConfJson(String path, String pkg, String vsn, String mclss, String prtp)
            throws IOException {
        File myObj = new File(path + "conf.jvt");
        try {
            if (myObj.createNewFile()) {
                System.out.println("conf.json: Success");
            }
        } catch (IOException e) {
            System.out.println("conf.json: An error occurred.");
            e.printStackTrace();
        }
        FileOutputStream fos = new FileOutputStream(myObj);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        if (pkg != "") {
            bw.write("package:" + pkg);
            bw.newLine();
        }
        bw.write("version:" + vsn);
        bw.newLine();
        bw.write("project_type:" + prtp);
        bw.newLine();
        bw.write("main_class:" + mclss);
        bw.newLine();
        bw.close();
    }

    public static Boolean createPath(String path) {
        File theDir = new File(path);
        if (!theDir.exists())
            theDir.mkdirs();
        return theDir.exists();
    }
}