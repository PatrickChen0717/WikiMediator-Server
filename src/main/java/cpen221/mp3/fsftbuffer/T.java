package cpen221.mp3.fsftbuffer;

public class T implements Bufferable{

    private String id;
    public T(String id){
        this.id=id;
    }



    @Override
    public String id() {
        return id;
    }
}
