package dev.neddslayer.sharedhealth.components;

import nerdhub.cardinal.components.api.component.Component;

public interface ISaturationComponent extends Component {
    float getSaturation();

    void setSaturation(float saturation);
}
