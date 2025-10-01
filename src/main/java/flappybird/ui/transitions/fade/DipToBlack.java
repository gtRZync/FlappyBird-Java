package flappybird.ui.transitions.fade;

public class DipToBlack extends FadeTransition {
    public DipToBlack(float duration) {
        this.transitionDuration = duration;
        this.speed = 1.f / transitionDuration;
    }
    public DipToBlack() {
    }

    @Override
    public void start() {
        super.start();
        transitionEvent = TransitionType.DIP_TO_BLACK;
    }
}