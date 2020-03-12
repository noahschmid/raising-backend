package ch.raising.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Service;

import ch.raising.data.IRepository;
import ch.raising.data.InvestorRepository;
import ch.raising.models.ErrorResponse;

/**
 * Class which helps creating and performing update queries
 * 
 * @author Noah Schmid
 * @version 1.0
 */
public class UpdateQueryBuilder {
    private String updates;
    private List fields;
    private int unitializedIntValue = -1; 
    private float unitializedFloatValue = -1f;
    private long id;
    private String tableName;
    private IRepository<?,?> repository;
    private JdbcTemplate jdbc;
    private String idField = "id";

    public UpdateQueryBuilder(String tableName, long id, IRepository<?,?> repository) {
        this.tableName = tableName;
        this.id = id;
        this.repository = repository;
        fields = new ArrayList<>();
    }

    public UpdateQueryBuilder(String tableName, long id, IRepository<?,?> repository, String idField) {
        this.tableName = tableName;
        this.id = id;
        this.repository = repository;
        fields = new ArrayList<>();
        this.idField = idField;
    }


    public void setJdbc(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Set default int value which represents null
     * @param val the value which represents null for integers
     */
    public void setUnitializedIntValue(int val) {
        this.unitializedIntValue = val;
    }

    /**
     * Set default float value which represents null
     * @param val the value which represents null for floats
     */
    public void setUnitializedFloatValue(float val) {
        this.unitializedFloatValue = val;
    }

    /**
     * Adds new field to update query. Field gets only updated, if field is not equal null
     * @param field the field which needs to be updated
     * @param fieldName the name of the field inside the database
     */
    public void addField(Object field, String fieldName) {
        if(field == null || fieldName == null)
            return;
        
        if(updates == null)
            updates = "";

        if(field instanceof Integer) {
            if((int)field != unitializedIntValue) {
                fields.add(field);
                if(updates != "")
                    updates += ", ";
                updates += fieldName + " = ?";
            }
        }

        if(field instanceof Float) {
            if((float)field != unitializedFloatValue) {
                fields.add(field);
                if(updates != "")
                    updates += ", ";
                updates += fieldName + " = ?";
            }
        }

        if(field instanceof String) {
            fields.add(field);
            if(updates != "")
                updates += ", ";
            updates += fieldName + " = ?";
        }
    }

    /**
     * Creates update query string
     * @return
     */
    public String buildQuery() {
        if(updates == null) 
            return null;

        return "UPDATE " + tableName + " SET " + updates + " WHERE " + idField + " = ?";
    }

    /**
     * Executes the finished query
     * @throws Exception
     */
    public void execute() throws Exception {
        if(buildQuery() == null)
            return;

        if(jdbc == null)
            throw new Error("Error while performing update query: jdbc not set.");

        String sql = buildQuery();

        try {
			jdbc.execute(sql, new PreparedStatementCallback<Boolean>(){  
				@Override  
				public Boolean doInPreparedStatement(PreparedStatement ps)  
						throws SQLException, DataAccessException {  
					
					for(int i = 1; i <= fields.size(); ++i) {
                        Object o = fields.get(i-1);
                        if(o instanceof String)
                            ps.setString(i, (String)o);
                        if(o instanceof Integer)
                            ps.setInt(i, (int)o);
                        if(o instanceof Float)
                            ps.setFloat(i, (float)o);
                    }
                    
                    ps.setLong(fields.size()+1, id);

					return ps.execute();  
				}  
            }); 
		} catch (Exception e) {
            if(e.getMessage() != null)
                throw new Error("Error while performing update query. Message: " + e.getMessage());
            throw new Error("Error while performing update query", e);
        }
    }
}