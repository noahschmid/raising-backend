package ch.raising.data;

public interface IRepository<Model, UpdateRequest> {
    public Model find(int id);
    
    public void update(int id, UpdateRequest updateRequest) throws Exception;
}