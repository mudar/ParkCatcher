package ca.mudar.parkcatcher.models;

import java.util.List;

public class PostProperties {
    private final List<Panel> panels;
    
    public PostProperties(List<Panel> panels) {
        this.panels = panels;
    }

    public List<Panel> getPanels() {
        return panels;
    }

    @Override
    public String toString() {
        return String.format("(panels: %s)", panels);
    }
}
