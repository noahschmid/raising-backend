package ch.raising.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties.Jdbc;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;

import ch.raising.data.InvestorRepository;
import ch.raising.interfaces.IRepository;
import ch.raising.models.responses.ErrorResponse;

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

	private JdbcTemplate jdbc;
    private String idField = "id";
    private String where = null;

	public UpdateQueryBuilder(JdbcTemplate jdbc, String tableName, long id) {
		this.tableName = tableName;
		this.id = id;
		fields = new ArrayList<>();
		this.jdbc = jdbc;
    }
    
    public UpdateQueryBuilder(String tableName, String where, JdbcTemplate jdbc) {
		this.tableName = tableName;
		this.where = where;
		this.jdbc = jdbc;
		fields = new ArrayList<>();
	}

	public UpdateQueryBuilder(JdbcTemplate jdbc, String tableName, long id, String idField) {
		this.tableName = tableName;
		this.id = id;
		fields = new ArrayList<>();
		this.idField = idField;
		this.jdbc = jdbc;
	}

	/**
	 * Set default int value which represents null
	 * 
	 * @param val the value which represents null for integers
	 */
	public void setUnitializedIntValue(int val) {
		this.unitializedIntValue = val;
	}

	/**
	 * Set default float value which represents null
	 * 
	 * @param val the value which represents null for floats
	 */
	public void setUnitializedFloatValue(float val) {
		this.unitializedFloatValue = val;
	}

	/**
	 * Adds new field to update query. Field gets only updated, if field is not
	 * equal null
	 * 
	 * @param field     the field which needs to be updated
	 * @param fieldName the name of the field inside the database
	 */
	public void addField(Object field, String fieldName) {
		if (field == null || fieldName == null)
			return;

		if (updates == null)
			updates = "";

		if (field instanceof Integer) {
			if ((int) field != unitializedIntValue) {
				fields.add(field);
				setFieldName(fieldName);
			}
		}

		if (field instanceof Float) {
			if ((float) field != unitializedFloatValue) {
				fields.add(field);
				setFieldName(fieldName);
			}
		}

		if (field instanceof String) {
			if (field != "" && field != null) {
				fields.add(field);
				setFieldName(fieldName);
			}
		}

		if (field instanceof Long) {
			if ((long) field != unitializedIntValue) {
				fields.add(field);
				setFieldName(fieldName);
			}
		}

		if(field instanceof Timestamp) {
			fields.add(field);
			setFieldName(fieldName);
		}

		else if(field instanceof Date) {
			fields.add(field);
			setFieldName(fieldName);
		}
	}

	
	public void updateLastChanged() {
		
	}
	
	private void setFieldName(String fieldName) {
		if (updates != "")
			updates += ", ";
		updates += fieldName + " = ?";
	}

	/**
	 * Creates update query string
	 * 
	 * @return
	 */
	public String buildQuery() {
		if (updates == null || updates == "")
			return "";

        if(where != null)
            return "UPDATE " + tableName + " SET " + updates + " WHERE " + where;
            
		return "UPDATE " + tableName + " SET " + updates + " WHERE " + idField + " = ?";
	}

	/**
	 * Executes the finished query
	 * 
	 * @throws DatabaseOperationException
	 * 
	 * @throws Exception
	 */
	public void execute() throws SQLException, DataAccessException {
		assert jdbc != null;
		String sql = buildQuery();
		if(sql == "" || sql == null) 
			return;
		
		jdbc.execute(buildQuery(), new PreparedStatementCallback<Boolean>() {
			@Override
			public Boolean doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {

				for (int i = 1; i <= fields.size(); ++i) {
					Object o = fields.get(i - 1);
					if (o instanceof String)
						ps.setString(i, (String) o);
					if (o instanceof Integer)
						ps.setInt(i, (int) o);
					if (o instanceof Float)
						ps.setFloat(i, (float) o);
					if (o instanceof Long)
						ps.setLong(i, (long) o);
					if (o instanceof Timestamp)
						ps.setTimestamp(i, (Timestamp) o);
					if(o instanceof Date)
						ps.setDate(i, (Date)o);
				}

				if(where == null)
					ps.setLong(fields.size() + 1, id);

				return ps.execute();
			}
		});
	}
}