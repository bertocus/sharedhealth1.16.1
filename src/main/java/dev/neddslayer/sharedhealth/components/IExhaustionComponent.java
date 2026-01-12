package dev.neddslayer.sharedhealth.components;

import nerdhub.cardinal.components.api.component.Component;

public interface IExhaustionComponent extends Component {
    float getExhaustion();

    void setExhaustion(float exhaustion);
}
