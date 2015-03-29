package nz.ac.aucklanduni.eyeatlas.util;

import android.content.Context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@Config(emulateSdk=18)
@RunWith(MyRobolectricTestRunner.class)
public class TestFileReader {

    private Context mockContext;

    private ShadowApplication mockApplication;
    private final static String FILE_PATH = "test/testreader.txt";
    private final static String EXPECTED_RESULT = "this is a test";

    @BeforeClass
    public static void oneTimeSetup() {}

    @Before
    public void setUp() {
        mockApplication = Robolectric.getShadowApplication();
        Assert.assertNotNull(this.mockApplication);
        mockContext = this.mockApplication.getApplicationContext();
        Assert.assertNotNull(mockContext);
    }

    @Test
    public void testFileReader() throws IOException {
        String result = FileReader.getFileContent(FILE_PATH, mockContext);
        assertThat(result, is(EXPECTED_RESULT));
    }

}