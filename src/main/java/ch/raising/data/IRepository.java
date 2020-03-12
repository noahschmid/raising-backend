package ch.raising.data;

public interface IRepository<Model, UpdateRequest> {
    public Model find(long id);
    
    public void update(long id, UpdateRequest updateRequest) throws Exception;

}