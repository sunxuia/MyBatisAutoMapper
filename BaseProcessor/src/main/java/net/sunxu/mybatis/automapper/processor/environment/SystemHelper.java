package net.sunxu.mybatis.automapper.processor.environment;

import com.google.inject.Singleton;
import net.sunxu.mybatis.automapper.processor.util.HelpUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import static com.google.common.base.Strings.isNullOrEmpty;
import static net.sunxu.mybatis.automapper.processor.util.AutoMapperException.newException;
import static net.sunxu.mybatis.automapper.processor.util.AutoMapperException.wrapException;


@Singleton
public class SystemHelper {

    public Set<Class<?>> getClassesInPackage(String packageName) {
        Set<Class<?>> classes = new HashSet<>();
        String packagePath = packageName.replace('.', '/');
        try {
            URL url = HelpUtils.class.getProtectionDomain().getCodeSource().getLocation();
            String path = url.getPath();
            if (path.endsWith("/")) { //save as file
                String filePath = URLDecoder.decode(path + packagePath, "UTF-8");
                File[] dirfiles = new File(filePath).listFiles(file -> file.getName().endsWith(".class"));
                for (File file : dirfiles) {
                    String className = file.getName().substring(0, file.getName().length() - 6); //remove ".class"
                    try {
                        classes.add(Class.forName(packageName + '.' + className));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } else { //save as a jar file
                JarFile jar =
                        ((JarURLConnection) new URL("jar:file:" + path + "!/" + packagePath).openConnection())
                                .getJarFile();
                for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements(); ) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (name.charAt(0) == '/') name = name.substring(1);
                    if (name.startsWith(packagePath)) {
                        if (name.endsWith(".class")) {
                            String className = name.substring(packageName.length() + 1, name.length() - 6);
                            try {
                                classes.add(Class.forName(packageName + '.' + className));
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    public File getFile(String fileName) {
        return new File(fileName);
    }

    public Document getDocument(String fileName) {
        return wrapException(() -> new SAXReader().read(new File(fileName)));
    }

    public void saveXMLFile(Document doc, String filePath) {
        File xmlFile = new File(filePath);
        XMLWriter writer = null;
        try {
            if (!xmlFile.exists()) {
                File directory = xmlFile.getParentFile();
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                xmlFile.createNewFile();
            }

            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            format.setTrimText(false);

            OutputStream outputStream = new FileOutputStream(xmlFile, false);
            writer = new XmlWriterWithLine(outputStream, format);
            writer.setEscapeText(false);
            writer.write(doc);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw newException(e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw newException(e);
            }
        }
    }

    private static class XmlWriterWithLine extends XMLWriter {
        public XmlWriterWithLine(OutputStream out, OutputFormat format) throws UnsupportedEncodingException {
            super(out, format);
        }

        @Override
        protected void writeNodeText(Node node) throws IOException {
            String text = node.getText();
            if (isNullOrEmpty(text)) {
                return;
            }
            StringBuilder sb = new StringBuilder(text.length() + 4);
            Element parent = node.getParent();
            if (parent != null && parent.content().get(0) == node) {
                if(!Pattern.matches("^\\s*\\n[\\s\\S]+", text)) {
                    sb.append("\n  ");
                }
            }
            sb.append(text);
            if (parent != null && parent.content().get(parent.nodeCount() - 1) == node) {
                if(!Pattern.matches("[\\s\\S]+\\n\\s*$", text)) {
                    sb.append("\n  ");
                }
            }

            if ((text != null) && (text.length() > 0)) {
                lastOutputNodeType = Node.TEXT_NODE;
                writer.write(sb.toString());
            }
        }
    }
}