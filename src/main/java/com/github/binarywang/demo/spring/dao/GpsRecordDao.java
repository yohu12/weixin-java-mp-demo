package com.github.binarywang.demo.spring.dao;

import com.github.binarywang.demo.spring.beans.Gps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author huyong
 * @since 2018/2/11
 */
@Service
public class GpsRecordDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int insert(Gps gps) {
        String sql = "INSERT INTO `gps` ( `lat`, `lon`, `user_code`, `create_date`,`start_flag`) VALUES" +
                " (?, ?, ?, ?,?)";
        int result = jdbcTemplate.update(sql, new Object[]{gps.getLat(), gps.getLon(), "1", new Date(), gps.getStartFlag()});
        return result;
    }

    public List<Gps> getByDate(Date startDate, Date endDate) {
        String sql = "SELECT * FROM gps WHERE date_format(create_date,'%Y-%c-%d') >= date_format(?,'%Y-%c-%d')  AND date_format(create_date,'%Y-%c-%d') <= date_format(?,'%Y-%c-%d')  ORDER BY create_date DESC";
        return jdbcTemplate.query(sql, new Object[]{startDate, endDate},
                (resultSet, i) -> {
                    Gps gps = new Gps();
                    gps.setLon(resultSet.getString("lon"));
                    gps.setLat(resultSet.getString("lat"));
                    return gps;
                });
    }

    public Gps getCurrentGps() {
        String sql = "SELECT * FROM gps   ORDER BY create_date DESC LIMIT 0,1 ";
        return jdbcTemplate.queryForObject(sql, (resultSet, rowNum) -> {
            Gps gps = new Gps();
            gps.setLon(resultSet.getString("lon"));
            gps.setLat(resultSet.getString("lat"));
            return gps;
        });
    }
}
