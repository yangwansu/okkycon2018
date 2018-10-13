package com.example.demo;

import java.util.List; /**
 * Created by yangwansu on 2018. 10. 9..
 */
public interface OptionRepository {
    List<Option> findByOptionIdIn(List<Long> optionIds);

    void save(Option option);
}
