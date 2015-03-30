package nz.ac.aucklanduni.eyeatlas.util;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Config(emulateSdk=18)
@RunWith(MyRobolectricTestRunner.class)
public class TestJsonParser {

    private Context mockContext;

    private ShadowApplication mockApplication;
    private final static String FILE_PATH = "test/testJsonParser.txt";
    private JsonObjectWalker walker;
    private JSONObject result;

    @BeforeClass
    public static void oneTimeSetup() {}

    @Before
    public void setUp() throws JSONException {
        mockApplication = Robolectric.getShadowApplication();
        Assert.assertNotNull(this.mockApplication);
        mockContext = this.mockApplication.getApplicationContext();
        Assert.assertNotNull(mockContext);
        result = JsonParser.getJsonObject(FILE_PATH, mockContext);
        walker = new JsonObjectWalker();
    }

    @Test
    public void testJsonParser() throws IOException, JSONException {
        assertThat(walker.getString(result, "object", "e"), is("f"));
    }

    @Test
    public void testJsonParserArray() throws IOException, JSONException {
        assertThat(walker.getString(result, "array"), is("[1,2,3]"));
    }
}
