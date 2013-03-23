package net.pedevilla.utils.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.UUID;

import javax.imageio.ImageIO;

public class ImageToJava {
	private static int MAX_ARRAY_SIZE = 2048;
	public static void main (String[] args) throws IOException {
		if(args.length != 2) {
			System.out.println("Parameters: <className> <image>");
			return;
		}
		String className = args[0];
		String fileName = args[1];
		
		StringBuilder classContent = new StringBuilder();
		StringBuilder classMap = new StringBuilder();
		
		classContent.append("import java.awt.image.BufferedImage;\n");
		classContent.append("import java.util.HashMap;\n");
		classContent.append("import java.io.File;\n");
		classContent.append("import java.io.IOException;\n");
		classContent.append("import javax.imageio.ImageIO;\n");
		classContent.append("import java.lang.reflect.InvocationTargetException;\n");
		classContent.append("import java.lang.reflect.Method;\n");
		classContent.append("/**\n");
		classContent.append(" * Created by Andrea Biaggi.\n");
		classContent.append(" * Please do not remove the comment\n");
		classContent.append(" * @author Andrea Biaggi\n");
		classContent.append(" * @version 1.0\n");
		classContent.append(" */\n");
		classContent.append("public class " + className + " {\n");
		classMap.append("\t\tHashMap<Integer, String> methodsMap = new HashMap<Integer, String>();\n");
		int classIndex = 0;
		BufferedImage source = ImageIO.read(new File(fileName));
		int arrayIndex = 0;
		int width = source.getWidth();
		int height = source.getHeight();
		classContent.append("\tprivate static int width = " + width + ";\n");
		classContent.append("\tprivate static int height = " + height + ";\n");
		classContent.append("\tprivate static int MAX_ARRAY_SIZE = " + MAX_ARRAY_SIZE + ";\n");
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int source_pixel = source.getRGB(x, y);
				if(arrayIndex == 0) {
					String privateClassName = "get_" + UUID.randomUUID().toString().replace('-', '_');
					classMap.append("\t\tmethodsMap.put(" + classIndex + ", \"" + privateClassName + "\");\n");
					classIndex++;
					classContent.append("\tpublic static int[] " + privateClassName + " (){\n");
					classContent.append("\t\treturn new int[] {" + source_pixel);
					arrayIndex++;
				} else if (arrayIndex == MAX_ARRAY_SIZE - 1) {
					classContent.append("," + source_pixel + "};\n\t}\n");
					arrayIndex = 0;
				} else {
					classContent.append("," + source_pixel);
					arrayIndex++;
				}
			}
		}
		// CLEAN UP
		if(arrayIndex != 0) {
			classContent.append("};\n\t}\n");
			arrayIndex = 0;
		}
		classContent.append("\tpublic static BufferedImage getImage() throws InstantiationException, IllegalAccessException, ClassNotFoundException,  IllegalArgumentException, InvocationTargetException, SecurityException, NoSuchMethodException {\n");
		classContent.append(classMap);
		classContent.append("\t\tBufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);\n");
		classContent.append("\t\tint arrayIndex = 0;\n");
		classContent.append("\t\tint classIndex = 0;\n");
		classContent.append("\t\tfor (int x = 0; x < width; x++) {\n");
		classContent.append("\t\t\tfor (int y = 0; y < height; y++) {\n");
		classContent.append("\t\t\t\tClass c = Class.forName(\"" + className + "\");\n");
		classContent.append("\t\t\t\tMethod m = c.getMethod(methodsMap.get(classIndex), null);\n");
		classContent.append("\t\t\t\tint pixel = ((int []) m.invoke(null, null))[arrayIndex];\n");
		classContent.append("\t\t\t\timage.setRGB(x, y, pixel);\n");
		classContent.append("\t\t\t\tif(arrayIndex == MAX_ARRAY_SIZE - 1) {\n");
		classContent.append("\t\t\t\t\tarrayIndex = 0;\n");
		classContent.append("\t\t\t\t\tclassIndex++;\n");
		classContent.append("\t\t\t\t} else {\n");
		classContent.append("\t\t\t\t\tarrayIndex++;\n");
		classContent.append("\t\t\t\t}\n");
		classContent.append("\t\t\t}\n");
		classContent.append("\t\t}\n");
		classContent.append("\t\treturn image;\n");
		classContent.append("\t}\n");
		
		classContent.append("\tpublic static void main(String args[]) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException  {\n");
		classContent.append("\t\tImageIO.write(getImage(), \"png\", new File(\"test.png\"));\n");
		classContent.append("\t}\n");
		
		classContent.append("}");
		Writer out = new OutputStreamWriter(new FileOutputStream(className + ".java"));
	    try {
	      out.write(classContent.toString());
	    }
	    finally {
	      out.close();
	      System.out.println( fileName + " embedded in " + className);
	    }
		
	}
}
