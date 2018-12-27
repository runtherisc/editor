package game;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;

import data.map.resources.Coords;
import data.map.resources.ImageResource;
import data.map.resources.MultiImageResourceAction;
import data.map.resources.Resource;
import gui.EditorGeneral;
import gui.ImageSliderHook;

public class ImageHelper {
	
	public static int DEFAULT_SIZE = 64;
	
	public static float PREFERRED_GRID_SIZE_MAX = 320F;
	
	private static int cleanupRetries = 10000;
//	private static int cleanupRetries = 10;
	
	public static StringBuilder getTempFolderPath(){
		
		return new StringBuilder(EditorGeneral.getWorkFolderPath()).append("/temp/");
	}
	
	public static StringBuilder getRevertFolderPath(){
		
		return getTempFolderPath().append("revert/");
	}
	
	public static void copyPngFromTempToResource(String prefix, int folderId, String postPath, String[] filenames, int total, List<String> imageOrder, String innerFolders, JFrame frame){
		
		copyPngFromFolderToResource(getTempFolderPath(), prefix, folderId, postPath, filenames, total, imageOrder, innerFolders, frame);
	}
	
	public static void copyPngFromRevertToResource(String prefix, int folderId, String postPath, String[] filenames, int total, String innerFolders, JFrame frame){
		
		copyPngFromFolderToResource(getRevertFolderPath(), prefix, folderId, postPath, filenames, total, null, innerFolders, frame);
	}
	
	private static void copyPngFromFolderToResource(StringBuilder folderName, String prefix, int folderId, String postPath, String[] filenames, int total, List<String> imageOrder, String innerFolders, JFrame frame){

		String resourcePath = getResourceFolder(prefix, folderId, postPath, innerFolders).toString();
		
		File resourceDir = new File(resourcePath);
		
		if(resourceDir.exists()) attemptDelete(resourceDir, true);
		attemptToCreate(resourceDir);
		
		String oldName = null;
		if(frame!=null) oldName = frame.getTitle();
		

		for (int i = 0; i < total; i++) {
			String filename = (filenames==null ? String.valueOf(i) : filenames[i]);
			String imageOrderName = (imageOrder==null ? String.valueOf(i) : imageOrder.get(i));
			System.out.println("pending save...read."+new StringBuilder(folderName).append(innerFolders.replace("/", "")).append(imageOrderName).append(".png").toString());
			String loadFile = new StringBuilder(folderName).append(innerFolders.replace("/", "")).append(imageOrderName).append(".png").toString();

			if(frame!=null) frame.setTitle("saving..." + filename + "/" + total);
			System.out.println("pending save...write."+new File(resourcePath, filename+".png").getAbsolutePath());
			
			if(!attemptCopyAndReplace(new File(loadFile), new File(resourcePath, filename+".png")))break;

		}

		
		if(frame!=null) frame.setTitle(oldName);
	}
	
	public static void copyMapsFromTempToMapsFolder(Set<String> filenames){
		
		String mapsFolder = new StringBuilder(EditorGeneral.getWorkFolderPath()).append("/").append(Resource.getMapPath()).toString();
		
		File mapDir = new File(mapsFolder);
		
		if(mapDir.exists()) attemptDelete(mapDir, true);
		attemptToCreate(mapDir);
		
		String tempFolder = getTempFolderPath().toString();
		
		for (String filename : filenames) {
			
			String pngFilename = filename + ".png";
	
			if(!attemptCopyAndReplace(new File(tempFolder, pngFilename), new File(mapDir, pngFilename))) break;
		}
	}
	
	public static boolean deleteResourceFolder(String prefix, int folderId, String postPath, String innerFolders){
		
		String resourcePath = getResourceFolder(prefix, folderId, postPath).toString();
		
		File resourceDir = new File(resourcePath);
		
		return attemptDelete(resourceDir);
	}
	
	public static StringBuilder getResourceFolder(String prefix, int folderId, String postPath){
		
		return getResourceFolder(prefix, folderId, postPath, "");
	}
	
	public static StringBuilder getResourceFolder(String prefix, int folderId, String postPath, String innerFolders){
		
//		System.out.println("pre postpath " + postPath);
		
//		if(postPath.startsWith(Resource.getCodename())){
//			postPath = postPath.substring(Resource.getCodename().length(), postPath.length());
//		}
		
		System.out.println("post postpath " + postPath);
		
		StringBuilder finalPath = new StringBuilder(EditorGeneral.getWorkFolderPath());
		
		if(!postPath.startsWith("/")) finalPath.append("/");
		
		finalPath.append(postPath).append(innerFolders);
		
		System.out.println("final path "+finalPath);
		
		if(prefix!=null && ((prefix.equals("0") && folderId<10) || !prefix.equals("0"))){
			finalPath.append(prefix);
		}
		
		if(folderId != -1) finalPath.append(folderId);

		if(finalPath.charAt(finalPath.length()-1) != '/') finalPath.append("/");
		
		return finalPath;
	}
	
	public static void copyPngFromReourceToTemp(String[] filenames, int total, String prefix, int folderId, String postPath, String innerFolders, JFrame frame){
		
		System.out.println("transfering images to temp folder");
		copyPngFromReourceToFolder(getTempFolderPath(), filenames, total, prefix, folderId, postPath, innerFolders, frame);
	}
	
	public static void copyPngFromReourceToRevert(String[] filenames, int total, String prefix, int folderId, String postPath, String innerFolders, JFrame frame){
		
		copyPngFromReourceToFolder(getRevertFolderPath(), filenames, total, prefix, folderId, postPath, innerFolders, frame);
	}
	
	private static void copyPngFromReourceToFolder(StringBuilder fileName, String[] filenames, int total, String prefix, int folderId, String postPath, String innerFolders, JFrame frame){
		

		File destFolder = new File(fileName.toString());
		
//			if(tempFolder.exists()) deleteTempFolder();
		if(!destFolder.exists()) attemptToCreate(destFolder);
		
		System.out.println("got files " + (destFolder.listFiles()==null));
		
		String oldName = null;
		if(frame!=null) oldName = frame.getTitle();
		
		StringBuilder loadDir = getResourceFolder(prefix, folderId, postPath, innerFolders);
		for (int i = 0; i < total; i++) {
			String filename = (filenames==null ? String.valueOf(i) : filenames[i]);
			String ext = filename.endsWith(".png") ? "" : ".png";
			String loadFile = new StringBuilder(loadDir).append(filename).append(ext).toString();
			System.out.println(loadFile);
			if(frame!=null) frame.setTitle("loading..." + filename + "/" + total);
//				Image image = ImageIO.read(new File(loadFile));
			File saveFile = new File(destFolder, new StringBuilder(innerFolders.replace("/", "")).append(filename).append(ext).toString());
//				if(saveFile.exists()) deleteSingleFileOrEmptyFolder(saveFile);
//				attemptToSave(image, saveFile);
			
			if(!attemptCopyAndReplace(new File(loadFile), saveFile))break;
		}
		
		if(frame!=null) frame.setTitle(oldName);

	}
	
	public static void copyFromTempToRevert(int total, String innerFolders, List<String> imageOrder, JFrame frame){
		
		StringBuilder revertPathSB = getRevertFolderPath();
		
		File revertFolder = new File(revertPathSB.toString());
		
		if(!revertFolder.exists()) attemptToCreate(revertFolder);
		
		imageCopy(getTempFolderPath(), revertPathSB, total, innerFolders, innerFolders, imageOrder, frame);
		

	}
	
	public static void copyFromRevertToTemp(int total, String innerFolders, List<String> imageOrder, JFrame frame){
		
		imageCopy(getRevertFolderPath(), getTempFolderPath(), total, innerFolders, innerFolders, null, frame);
		
	}
	
	public static void duplicateFilesInTemp(int total, String sourceInnerFolders, String destInnerFolders, JFrame frame){
		
		imageCopy(getTempFolderPath(), getTempFolderPath(), total, sourceInnerFolders, destInnerFolders, null, frame);
		
	}
	
	private static void imageCopy(StringBuilder sourceDir, StringBuilder destDir, int total, String sourceInnerFolders, String destInnerFolders, List<String> imageOrder, JFrame frame){
		
		sourceInnerFolders = sourceInnerFolders.replace("/", "");
		destInnerFolders = destInnerFolders.replace("/", "");
		
		String oldName = null;
		if(frame!=null) oldName = frame.getTitle();

		for (int i = 0; i < total; i++) {

			StringBuilder sourceSB = new StringBuilder(sourceInnerFolders);
			StringBuilder destSB = new StringBuilder(destInnerFolders);

			if(imageOrder!=null){
				sourceSB.append(imageOrder.get(i));
				sourceSB.append(".png");
				destSB.append(i);
				destSB.append(".png");
			}else{
				sourceSB.append(i);
				sourceSB.append(".png");
				destSB.append(i);
				destSB.append(".png");
			}

			if(frame!=null) frame.setTitle("copying..." + sourceSB);
			
			System.out.println("copying " + new StringBuilder(sourceDir).append(sourceSB).toString() +
			                   " to " + new StringBuilder(destDir).append(destSB).toString());
			
			if(!attemptCopyAndReplace(new File(new StringBuilder(sourceDir).append(sourceSB).toString()),
								  new File(new StringBuilder(destDir).append(destSB).toString()))) break;

		}
		
		if(frame!=null) frame.setTitle(oldName);
	}
	
	
	public static boolean renameImageFolder(String oldDir, String newDir){
		
		File newName = new File(getResourceFolder(null, -1, newDir).toString());
		File oldName = new File(getResourceFolder(null, -1, oldDir).toString());
		
		if(!oldName.exists()) return false;
		if(newName.exists()) return false;
		
		return oldName.renameTo(newName);
		
	}
	
	//kinda image related
	public static Coords getSpanFromTextboxes(JSpinner spanX, JSpinner spanY){

		int spanXvalue = (int) spanX.getValue();
		int spanYvalue = (int) spanY.getValue();
		
		if(spanXvalue > 0 && spanYvalue > 0){
			
			return new Coords(spanXvalue, spanYvalue);
		}
		
		return null;
	}
	
	//used by LevelDataIO too... maybe move all attempts to a generic place
	public static boolean attemptCopyAndReplace(File source, File dest){
		
		int trys = 0;
		int retrymultipler = 1;
		
		boolean failed = true;
		
		while(failed){
		
			if(trys==cleanupRetries*retrymultipler){
				boolean keeptrying = readWriteIssue();	
				if(!keeptrying) return false;
				retrymultipler++;
			}
			
			try {
				 
//				System.out.println("source:"+source.getAbsolutePath());
//				System.out.println("dest:"+dest.getAbsolutePath());
				Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
				failed = false;
				
			} catch (IOException e) {
	
				System.out.println("The above exception is an issue on slow on slow machines :| Trying again");
				trys++;
			}
		}
		 
		 return true;
	}
	
	private static boolean attemptDelete(File dirToDelete){
		
		return attemptDelete(dirToDelete, false);
	}
	
	public static boolean attemptDelete(File dirToDelete, boolean filesOnly){
		
			boolean success = deleteFilesInFolder(dirToDelete, filesOnly);
			
			if(success && !filesOnly) success = deleteSingleFileOrEmptyFolder(dirToDelete);
			
			return success;

	}
	
	public static boolean readWriteIssue(){
		
		int result = JOptionPane.showConfirmDialog(null, "Slow file read write detected, Images may not be deplayed correctly if cancelled.  Keep trying?", "Read Write Issue", JOptionPane.WARNING_MESSAGE);
		
		if(result==JOptionPane.OK_OPTION){
			return true;
		}
		
		return false;
	}
	
	public static boolean deleteTempFolder(){
		
		System.out.println("deleting temp folder");
		
		deleteRevertFolder();
		
		File temp = new File(getTempFolderPath().toString());
		
		return attemptDelete(temp);
	}
	
	public static boolean deleteRevertFolder(){
		
		System.out.println("deleting revert folder");
		
		File revert = new File(getRevertFolderPath().toString());
		
		return attemptDelete(revert);
	}
	
	public static boolean attemptToCreate(File folder){
		
		int trys = 0;
		int retrymultipler = 1;
		
		//not the best solution :|
		while(!folder.exists() && !folder.canWrite()){
			
			folder.mkdirs();
			
			if(trys==cleanupRetries*retrymultipler){
				boolean keeptrying = readWriteIssue();	
				if(!keeptrying) return false;
				retrymultipler++;
			}
			
			trys++;
		}
		
		return true;
	}
	
	private static boolean attemptToSave(Image image, File saveFile){
		
		int trys = 0;
		int retrymultipler = 1;
		
		boolean failed = true;
		
		while(failed){
		
			try{
				
				if(trys==cleanupRetries*retrymultipler){
					boolean keeptrying = readWriteIssue();	
					if(!keeptrying) return false;
					retrymultipler++;
				}
				
				ImageIO.write((BufferedImage)image, "PNG",  saveFile);
				
				failed = false;
				
			}catch(Exception e){
				
				System.out.println("The above exception is an issue on slow on slow machines :| Trying again");
				trys++;
			}
		}
		
		return true;
	}
	
	private static boolean deleteFilesInFolder(File folder, boolean filesOnly){
		
		File[] files = folder.listFiles();
		
		boolean success = true;
		
		if(files!=null){
			for (File file : files) {
				if(!filesOnly || (filesOnly && !file.isDirectory())){
					success = deleteSingleFileOrEmptyFolder(file);
					if(!success) break;
				}
				
			}
		}
		
		return success;
	}
	
	public static boolean deleteSingleFileOrEmptyFolder(File file){
		
		int trys = 0;
		int retrymultipler = 1;
		
		if(file.isDirectory() && file.list().length > 0 ){
			System.out.println("is directory that is not empty");
			return false;
		}
		
		//not the best solution :|
		while(file.exists()){
			
//			System.out.println("attempt "+trys);
			
			if(trys==cleanupRetries*retrymultipler){
				boolean keeptrying = readWriteIssue();
				if(!keeptrying) return false;
				retrymultipler++;
			}
			
			file.delete();
			
			trys++;
		}
		
		return true;
	}
	
	public static void displayImagesAndButtons(StringBuilder resourceFolder, Coords span, JPanel imagePanel, List<String> imageOrder, int imageTotal, int maxTotal, int offSet, String innerFolders){
		
		displayImagesAndButtons(resourceFolder, span, imagePanel, imageOrder, imageTotal, maxTotal, offSet, innerFolders, null);
	}
	
	public static void displayImagesAndButtons(StringBuilder resourceFolder, Coords span, JPanel imagePanel, List<String> imageOrder, int imageTotal, int maxTotal, int offSet, String innerFolders, String[] labelNames){
		
		JPanel topPanel = (JPanel) imagePanel.getComponent(0);
		
		int maxDisplay = (maxTotal> 8 ? 8 : maxTotal);
		
		for (int i = 0; i < maxDisplay; i++) {
			
			if(maxTotal > 8){
				JLabel label = (JLabel) topPanel.getComponent(i*3);
				label.setText("image " + (i + offSet));

			}else if(labelNames!=null && i < labelNames.length){//only needed if labels change on a form
				
				JLabel label = (JLabel) topPanel.getComponent(i*3);
				label.setText(labelNames[i]);
			}

			JLabel label = (JLabel) topPanel.getComponent(i*3+1);
			if(label!=null){

				Image image = null;
				
				if(i + offSet < imageTotal){
	
					String filePath = new StringBuilder(resourceFolder)
												.append(innerFolders.replace("/", ""))
												.append(imageOrder.get(i + offSet))
												.append(".png").toString();
					
					System.out.println("displaying image "+filePath);
			        
					try {
						image = ImageHelper.resizeImage(ImageIO.read(new File(filePath)), span);
	
					} catch (IOException e) {
		
						e.printStackTrace();
					}
				}else{
			        image = new BufferedImage(ImageHelper.DEFAULT_SIZE,ImageHelper.DEFAULT_SIZE,BufferedImage.TYPE_INT_RGB);
//			        System.out.println("displaying blank image");
				}
				label.setIcon(new ImageIcon(image));
				
				JButton button = (JButton) topPanel.getComponent(i*3+2);
				
				if(i + offSet + 1 < imageTotal)button.setText("Swap >");
				else if(i + offSet + 1 == imageTotal)button.setText("Clear");
				else if(i + offSet == imageTotal)button.setText("Add");
				else button.setText("");
				
//				System.out.println("just set button text "+button.getText());
			}else System.err.println("Label was null on add images!");
		}
		
		if(imagePanel.getComponentCount()==2){
			
			JSlider imageSlider = (JSlider) imagePanel.getComponent(1);
			int max = (int) Math.floor((imageTotal-1)/4) * 4;
			if(max > 0) imageSlider.setMaximum(max);
			else imageSlider.setMaximum(0);
			
			int spacing = imageTotal > 40 ? (int)(Math.floor((imageTotal-1)/40) * 4) : 4;
			
			imageSlider.setMajorTickSpacing(spacing);
			imageSlider.setLabelTable(imageSlider.createStandardLabels(spacing));

		}
		
		imagePanel.repaint();
	}
	
	public static void displaySingleImageOnLabelFromTemp(JLabel destLabel, String innerFolder, String filename, Coords span){
		
		String filePath = new StringBuilder(getTempFolderPath())
				.append(innerFolder.replace("/", ""))
				.append(filename)
				.append(".png").toString();
		
		try {
			System.out.println(filePath);
			Image image = ImageHelper.resizeImage(ImageIO.read(new File(filePath)), span);
			
			destLabel.setIcon(new ImageIcon(image));

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public static Image resizeImage(Image source, Coords span){
		
		BufferedImage resizedImage = new BufferedImage(ImageHelper.DEFAULT_SIZE,ImageHelper.DEFAULT_SIZE,BufferedImage.TYPE_INT_RGB);
		Graphics2D g = resizedImage.createGraphics();
		
		int xOffset = 0;
		int yOffset = 0;
		
		int xFinalSize = 64;
		int yFinalSize = 64;
		
		if(span.x() > span.y()){
			
			yFinalSize = calculateResizedLength(span.x(), span.y());
			yOffset = calculateOffset(yFinalSize);
			
		}else if(span.x() < span.y()){
			
			xFinalSize = calculateResizedLength(span.y(), span.x());
			xOffset = calculateOffset(xFinalSize);
		}
		
		
		g.drawImage(source, xOffset, yOffset, xFinalSize, yFinalSize, null);
		g.dispose();
		
		return resizedImage;
	}

	public static BufferedImage[][] getImageGridFromStatic(Coords span){
		
		File staticImage = new File(getTempFolderPath().append("static.png").toString());
		
		if(!staticImage.exists()){
			System.out.println("unable to find static image in temp dir");
			return null;
		}
		
		BufferedImage[][] grid = new BufferedImage[span.x][span.y];
		
		int girdBoxSize;
		
		if(span.x() > span.y()){
			
			girdBoxSize = Math.round(PREFERRED_GRID_SIZE_MAX/(float)span.x);
			
		}else{
			
			girdBoxSize = Math.round(PREFERRED_GRID_SIZE_MAX/(float)span.y);
		}
		
		System.out.println("girdBoxSize"+girdBoxSize);
		
		int actualXSize = span.x * girdBoxSize;
		int actualYSize = span.y * girdBoxSize;
		
		System.out.println("actualXSize"+actualXSize+"actualYSize"+actualYSize);

		BufferedImage masterImage = null;
		
		try {
			masterImage = new BufferedImage(actualXSize, actualYSize, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = masterImage.createGraphics();
			g.drawImage(ImageIO.read(staticImage), 0, 0, actualXSize, actualYSize, null);
			g.dispose();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < span.x; i++) {
			for (int j = 0; j < span.y; j++) {
			
				
				grid[i][j] = new BufferedImage(girdBoxSize, girdBoxSize, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = grid[i][j].createGraphics();
				g.drawImage(masterImage.getSubimage(i*girdBoxSize, j*girdBoxSize, girdBoxSize, girdBoxSize), 0, 0, girdBoxSize, girdBoxSize, null);
				g.dispose();
				 
			}
		}
		
		return grid;
		
	}
	
	public static Image addBlueMaskToImage(BufferedImage image){
		
		return addColourMaskToImage(image, Color.BLUE);
	}
	
	public static Image addRedMaskToImage(BufferedImage image){
		
		return addColourMaskToImage(image, Color.RED);
	}
	
	public static Image addColourMaskToImage(BufferedImage image, Color color){
		
		BufferedImage overlay = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = overlay.createGraphics();
		g.setPaint(color);
		g.fillRect(0, 0, overlay.getWidth(), overlay.getHeight());
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));	
		g.drawImage(overlay, 0, 0, null);		
		g.drawImage(image, 0, 0, null);
		
		return overlay;
	}
	
	public static boolean doesFileExistInTemp(File file){
		
		if(file==null) return false;
		
		String tempFolder = getTempFolderPath().toString();
		
		return new File(tempFolder.toString(), file.getName()).exists();
	}
	
	public static boolean addSingleFileToTemp(File file){
		
		boolean success = false;
		
		String tempFolder = getTempFolderPath().toString();
		
		File folder = new File(tempFolder);
		attemptToCreate(folder);
		
		Image image;
		try {
			image = ImageIO.read(file);

			success = attemptToSave(image, new File(folder, file.getName()));
		} catch (IOException e) {

			e.printStackTrace();
		}
		
		return success;
		
	}
	
	public static int addSelectedFilesToTemp(List<File> files, String[] filenames, int total, List<String> imageOrder, int imageTotal, String innerFolders, JFrame frame){
		
		String tempFolder = getTempFolderPath().toString();
		
		File folder = new File(tempFolder);
		attemptToCreate(folder);
		
		int f = 0;
		
		String frameTitle = frame.getTitle();

		for(int i = imageTotal; (i < total && i < files.size()+imageTotal); i++){
			
			try{
				String filename;
				if(i < imageOrder.size()){
					filename = imageOrder.get(i);
				}else{
					filename = (filenames==null ? String.valueOf(i) : filenames[i]);
					imageOrder.add(filename);
				}
				File sourceFile = files.get(f++);
				frame.setTitle("copying "+sourceFile.getName());
				Image image = ImageIO.read(sourceFile);
				String saveFile = new StringBuilder(tempFolder).append(innerFolders.replace("/", "")).append(filename).append(".png").toString();
//				ImageIO.write((BufferedImage)image, "PNG", new File(saveFile));
				attemptToSave(image, new File(saveFile));
				
				
			} catch (IOException e) {
				
				e.printStackTrace();
			}

		}
		
		frame.setTitle(frameTitle);
		
		return files.size()+imageTotal > total ? total : files.size()+imageTotal;
		
	}
	
	public static boolean imageButtonClicked(int buttonPos, int offset, List<String> imageOrder, int imageTotal, Coords span, JPanel imagePanel, String[] filenames, int maxTotal, String innerFolders, ImageSliderHook hook, JFrame frame){
		
		int imagePos = buttonPos + offset;
		
		boolean saveableChanges = false;
		
		if(imagePos+1 < imageTotal){
			
			String im = imageOrder.get(imagePos);
			imageOrder.set(imagePos, imageOrder.get(imagePos+1));
			imageOrder.set(imagePos+1, im);
			displayImagesAndButtons(ImageHelper.getTempFolderPath(), span, imagePanel, imageOrder, imageTotal, maxTotal, offset, innerFolders);
			
			saveableChanges = true;
			System.out.println("swap with button " + (imagePos+1));
			
		}else if(imagePos+1 == imageTotal){

			imageTotal--;
			hook.updatedImageTotal(imageTotal);
//			imageOrder.remove(imageOrder.size()-1);
			System.out.println("clear image on " + imagePos);
			displayImagesAndButtons(ImageHelper.getTempFolderPath(), span, imagePanel, imageOrder, imageTotal, maxTotal, offset, innerFolders);

			saveableChanges = true;
			
		}else if(imagePos == imageTotal){		

			System.out.println("add image on " + imagePos);
			List<File> files = new FileBrowser().browserMultiPngDiag(ConfigIO.getProperty(ConfigIO.LAST_IMAGE_LOAD_KEY));

			if(files != null && files.size() > 0){
				imageTotal = addSelectedFilesToTemp(files, filenames, maxTotal, imageOrder, imageTotal, innerFolders, frame);
				hook.updatedImageTotal(imageTotal);
				displayImagesAndButtons(ImageHelper.getTempFolderPath(), span, imagePanel, imageOrder, imageTotal, maxTotal, offset, innerFolders);
				saveableChanges = true;
			}
		}else{
			
			System.out.println("do nothing");

		}
		
		return saveableChanges;

	}
	
	public static boolean isStaticImageAddedToTemp(){
		
		return new File(getTempFolderPath().append("static.png").toString()).exists();
	}
	
	public static String getStaticFilePath(String postPath){
		
		return getResourceFolder(null, -1, postPath).append("static.png").toString();
	}
	
	public static void deleteImageFolder(ImageResource imageResource){
		
		String dir = imageResource.getDirectory();
		
		deleteIdleImageFolder(imageResource);
		deleteCreationImageFolder(imageResource);
		deleteDestrutionImageFolder(imageResource);
		deleteActionImageFolder(imageResource);
		deleteMovementImageFolder(dir);
		attemptDelete(new File(getResourceFolder(null, -1, dir).toString()));
	}
	
	public static boolean deleteStaticImage(String dir){
		
		return new File(getResourceFolder(null, -1, dir).append("static.png").toString()).delete();
	}
	
	public static void deleteIdleImageFolder(ImageResource imageResource){
		
	    List<MultiImageResourceAction> idleList = imageResource.getIdles();
	    
	    String baseDir = getResourceFolder(null, -1, imageResource.getDirectory()).append("idle/sequence").toString();
		
	    for(MultiImageResourceAction idle : idleList){
	
	    	attemptDelete(new File(baseDir + idle.getDirectory()));
	    }
	    
	    attemptDelete(new File(getResourceFolder(null, -1, imageResource.getDirectory()).append("idle/").toString()));

	}
	
	public static void deleteCreationImageFolder(ImageResource imageResource){
		
		String dir = imageResource.getDirectory();
		
		attemptDelete(new File(getResourceFolder(null, -1, dir).append("creation/").toString()));
	}
	
	public static void deleteDestrutionImageFolder(ImageResource imageResource){
		
		String dir = imageResource.getDirectory();

		attemptDelete(new File(getResourceFolder(null, -1, dir).append("destruction/").toString()));
	}
	
	public static void deleteActionImageFolder(ImageResource imageResource){
		
	    List<MultiImageResourceAction> busyList = imageResource.getBusy();
	    
	    String baseDir = getResourceFolder(null, -1, imageResource.getDirectory()).append("action/sequence").toString();
		
	    for(MultiImageResourceAction busy : busyList){
	
	    	attemptDelete(new File(baseDir + busy.getDirectory()));
	    }
	    
	    attemptDelete(new File(getResourceFolder(null, -1, imageResource.getDirectory()).append("action/").toString()));
	}
	
	public static void deleteMovementImageFolder(String dir){
		
		String baseDir = getResourceFolder(null, -1, dir).append("movement/").toString();
		attemptDelete(new File(baseDir + "down/"));
		attemptDelete(new File(baseDir + "up/"));
		attemptDelete(new File(baseDir + "left/"));
		attemptDelete(new File(baseDir + "right/"));
		attemptDelete(new File(baseDir));
	}

	public static void processImageOrderMaps(Map<String, Integer> childRevertMap, String dir, JFrame frame){
					
		for (String innerFolder : childRevertMap.keySet()) {
			
			int total = childRevertMap.get(innerFolder);
			
			//static up these constants 
			String[] filenames = null;
			
			if(innerFolder == "*"){
				innerFolder = "";
			}else if(innerFolder=="movement/"){
				filenames = new String[]{"10", "11", "20", "21", "30", "31", "40", "41"};
			}
			
			ImageHelper.copyPngFromRevertToResource(null, -1, dir, filenames, total, innerFolder, frame);
			
		}
		
	}
	
	private static int calculateResizedLength(int longer, int shorter){
		
		return DEFAULT_SIZE / longer * shorter;
		
	}
	
	private static int calculateOffset(int resized){
		
		return (DEFAULT_SIZE - resized) / 2;
	}

}
