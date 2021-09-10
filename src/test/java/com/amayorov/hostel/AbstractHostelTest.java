package com.amayorov.hostel;


import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureTestDatabase
@SpringBootTest(classes = {HostelApplication.class})
public abstract class AbstractHostelTest {
}
