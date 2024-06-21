package io.github.lorisdemicheli.minecraft_package;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

@Mojo(name = "build-jar", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class BuildJarMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject project;
	@Parameter(defaultValue = "${session}", readonly = true, required = true)
	private MavenSession session;
	@Parameter(defaultValue = "${project.build.directory}/classes/plugin.yml")
	private File pluginFile;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (!pluginFile.exists()) {
			throw new MojoExecutionException("plugin.yml file not found: " + pluginFile);
		}

		try {
			buildPluginFile();
			getLog().info("plugin.yml has been modified and is ready for packaging.");
		} catch (IOException e) {
			throw new MojoExecutionException("Error processing plugin.yml", e);
		}
	}

	private void buildPluginFile() throws IOException {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);
        
        Yaml yaml = new Yaml(representer, options);
		Map<String, Object> data = yaml.load(new FileInputStream(pluginFile));
		List<String> dependencies = new ArrayList<>();
		for (Dependency dependency : project.getDependencies()) {
			if(isValidDependency(dependency)) {
				String value = dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion();
				dependencies.add(value);
			}
		}
		data.put("libraries", dependencies);
		yaml.dump(data, new FileWriter(pluginFile));
	}
	
	private boolean isValidDependency(Dependency dependency) {
		if(dependency.isOptional()) {
			return false;
		} else {
			switch (dependency.getScope()) {
			case "compile":
			case "runtime":
				return true;
			case "test":
			case "system": 
			case "provided":
				return false;
			default:
				return false;
			}
		}
	}

}
