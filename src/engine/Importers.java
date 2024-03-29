package engine;

import library.audio.AudioDecoderLibrary;
import library.audio.VorbisDecoder;
import library.font.AngelcodeFontImporter;
import library.font.FontImporterLibrary;
import library.models.AnimationImporterLibrary;
import library.models.ModelImporterLibrary;
import library.models.importers.AssimpModelImporter;
import library.models.importers.ColladaAnimationImporter;
import library.models.importers.ColladaModelImporter;
import library.models.importers.MD5AnimationImporter;
import library.models.importers.MD5ModelImporter;
import library.models.importers.OBJModelImporter;

public class Importers {
	
	public static void register() {
		ModelImporterLibrary.registerImporter(new AssimpModelImporter());
		ModelImporterLibrary.registerImporter(new ColladaModelImporter());
		ModelImporterLibrary.registerImporter(new MD5ModelImporter());
		ModelImporterLibrary.registerImporter(new OBJModelImporter());
		
		AnimationImporterLibrary.registerImporter(new ColladaAnimationImporter());
		AnimationImporterLibrary.registerImporter(new MD5AnimationImporter());
		
		AudioDecoderLibrary.registerDecoder(new VorbisDecoder());
		
		FontImporterLibrary.registerImporter(new AngelcodeFontImporter());
	}

}
