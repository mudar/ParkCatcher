
package ca.mudar.parkcatcher.models;

public class Panel {
    private final String desc;
    private final String cat;

    public Panel(String desc, String cat) {
        this.desc = desc;
        this.cat = cat;
    }

    public String getDesc() {
        return desc;
    }

    public String getCat() {
        return cat;
    }

    @Override
    public String toString() {
        return String.format("(panel: %s. cat: %s)", desc, cat);
    }
}
