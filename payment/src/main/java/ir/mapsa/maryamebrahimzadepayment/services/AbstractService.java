package ir.mapsa.maryamebrahimzadepayment.services;

import ir.mapsa.maryamebrahimzadepayment.exceptions.IdNotFoundException;
import ir.mapsa.maryamebrahimzadepayment.exceptions.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public abstract class AbstractService<R extends JpaRepository<E,Long>,E> {
    @Autowired
    protected R repository;

    public void add( E e)throws ServiceException{
        repository.save(e);
    }

    public void update( E e)throws ServiceException{
        repository.save(e);
    }

    public void removeById(Long id)throws ServiceException {
        if(this.findById(id).isEmpty()){
            throw new IdNotFoundException("id-not-found");
        }
        repository.deleteById(id);
    }

    public Optional<E> findById(Long id)throws ServiceException{
        if(repository.findById(id).isEmpty()){
            throw new IdNotFoundException("id-not-found");
        }
        return repository.findById(id);
    }

    public List<E> getAll(){
        return repository.findAll();
    }

    public List<E> findByExample(E e){
        return repository.findAll(Example.of(e));
    }

}
