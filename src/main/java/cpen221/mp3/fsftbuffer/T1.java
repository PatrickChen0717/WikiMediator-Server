package cpen221.mp3.fsftbuffer;

public class T1 implements Bufferable {


    private String id;
    public T1(String id){
        this.id=id;
    }


    @Override
    public String id() {
        return id;
    }
}
