package dev.neddslayer.sharedhealth.components;

import nerdhub.cardinal.components.api.component.Component;

public interface IHungerComponent extends Component {
    int getHunger();

    void setHunger(int hunger);
}
