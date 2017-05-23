package library.models.importers;

import java.io.InputStream;

import library.models.Animation;
import library.models.AnimationImporter;
import library.models.KeyFrameData;
import library.models.collada.AnimationLoader;
import library.models.collada.ColladaAnimation;
import library.models.collada.ColladaKeyFrameData;
import utils.XMLNode;
import utils.XMLParser;

public class ColladaAnimationImporter implements AnimationImporter {

	private String[] EXTENSIONS = { "dae" };
	
	@Override
	public String[] getExtensions() {
		return EXTENSIONS;
	}

	@Override
	public Animation importAnimation(InputStream stream, String animation) {
		XMLNode node = XMLParser.loadXmlFile(stream);
		XMLNode animNode = node.getChild("library_animations");
		XMLNode jointsNode = node.getChild("library_visual_scenes");
		AnimationLoader loader = new AnimationLoader(animNode, jointsNode, animation);
		ColladaAnimation animData = loader.extractAnimation();
		Animation animationInstance = new Animation(animData.getLengthSeconds(), convertKeyFrames(animData.getKeyFrames()));
		return animationInstance;
	}
	
	private KeyFrameData[] convertKeyFrames(ColladaKeyFrameData[] frames) {
		KeyFrameData[] keyFrames = new KeyFrameData[frames.length];
		for (int i = 0; i < keyFrames.length; i++) {
			keyFrames[i] = new KeyFrameData(frames[i].getTime(), frames[i].getJointTransforms());
		}
		return keyFrames;
	}

}
