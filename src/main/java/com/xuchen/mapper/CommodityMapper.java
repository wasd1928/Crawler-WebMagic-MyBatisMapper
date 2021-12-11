package com.xuchen.mapper;
import com.xuchen.pojo.Commodity;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CommodityMapper {

    void addOne(Commodity commodity);

    List<Commodity> selectAll();

    void exampleAdd();

    void clearAll();
}
