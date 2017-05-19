package engine;

import library.audio.AudioDecoderLibrary;
import library.audio.VorbisDecoder;
import library.font.AngelcodeFontImporter;
import library.font.FontImporterLibrary;
import library.models.AnimationImporterLibrary;
import library.models.ColladaAnimationImporter;
import library.models.ColladaModelImporter;
import library.models.ModelImporterLibrary;
import library.models.OBJModelImporter;

public class Importers {
	
	public static void register() {
		ModelImporterLibrary.registerImporter(new OBJModelImporter());
		ModelImporterLibrary.registerImporter(new ColladaModelImporter());
		
		AnimationImporterLibrary.registerImporter(new ColladaAnimationImporter());
		
		AudioDecoderLibrary.registerDecoder(new VorbisDecoder());
		
		FontImporterLibrary.registerImporter(new AngelcodeFontImporter());
	}

}
