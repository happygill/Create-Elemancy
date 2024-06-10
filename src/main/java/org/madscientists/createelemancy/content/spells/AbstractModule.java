package org.madscientists.createelemancy.content.spells;

public abstract class AbstractModule {

    private final String name;
    private final  String description;
    private final ModuleType type;

    protected AbstractModule(String name, String description, ModuleType type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public enum ModuleType{
        EFFECT,
        MODIFIER
    }

    public ModuleType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }


}
