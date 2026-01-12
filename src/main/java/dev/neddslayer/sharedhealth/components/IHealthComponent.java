package dev.neddslayer.sharedhealth.components;

import nerdhub.cardinal.components.api.component.Component;

public interface IHealthComponent extends Component {
    float getHealth();

    void setHealth(float health);
}