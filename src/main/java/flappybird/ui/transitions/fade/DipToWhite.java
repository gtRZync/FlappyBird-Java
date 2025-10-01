package flappybird.ui.transitions.fade;

public class DipToWhite extends FadeTransition {
    public DipToWhite(float duration) {
        this.transitionDuration = duration;
        this.speed = 1.f / transitionDuration;
    }
    public DipToWhite() {
    }

    @Override
    public void start() {
        super.start();
        transitionEvent = TransitionType.DIP_TO_WHITE;
    }
}