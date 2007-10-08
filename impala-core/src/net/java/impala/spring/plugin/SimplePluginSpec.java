package net.java.impala.spring.plugin;

import java.util.Collection;

import org.springframework.util.Assert;

public class SimplePluginSpec implements PluginSpec {

	private String name;

	private ChildSpecContainer childContainer;
	
	public SimplePluginSpec(String name) {
		super();
		Assert.notNull(name);
		this.name = name;
		this.childContainer = new ChildSpecContainerImpl();
	}
	
	public SimplePluginSpec(ChildSpecContainer parent, String name) {
		super();
		Assert.notNull(name);
		Assert.notNull(parent);
		this.name = name;
		this.childContainer = new ChildSpecContainerImpl();
		parent.add(this);
	}

	public String getName() {
		return name;
	}
	
	public Collection<String> getPluginNames() {
		return childContainer.getPluginNames();
	}
	
	public PluginSpec getPlugin(String pluginName) {
		return childContainer.getPlugin(pluginName);
	}
	
	public Collection<PluginSpec> getPlugins() {
		return childContainer.getPlugins();
	}

	public boolean hasPlugin(String pluginName) {
		return getPlugin(pluginName) != null;
	}

	public void add(PluginSpec pluginSpec) {
		childContainer.add(pluginSpec);
	}

	public PluginSpec remove(String pluginName) {
		return childContainer.remove(pluginName);
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((childContainer == null) ? 0 : childContainer.hashCode());
		result = PRIME * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final SimplePluginSpec other = (SimplePluginSpec) obj;
		if (childContainer == null) {
			if (other.childContainer != null)
				return false;
		}
		else if (!childContainer.equals(other.childContainer))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}



}
