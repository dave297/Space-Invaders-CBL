public class DefaultInvader extends Invader{
    public DefaultInvader(int startX, int startY) {
        super(startX, startY);
    }
    
    @Override
    public void update(int panelWidth, int tick, int groupDirection, int groupSpeed) {

    }
    @Override
    public boolean canDropOnBounce() {
        return true;
    }

    @Override
    public void takeHit() {
        super.kill();
    }
}
