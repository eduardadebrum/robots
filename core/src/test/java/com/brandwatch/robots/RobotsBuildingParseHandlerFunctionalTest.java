package com.brandwatch.robots;

import com.brandwatch.robots.domain.*;
import com.brandwatch.robots.matching.ExpressionCompiler;
import com.brandwatch.robots.matching.Matcher;
import com.brandwatch.robots.parser.ParseException;
import com.brandwatch.robots.parser.RobotsParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.Reader;

import static com.brandwatch.robots.AbstractDataTest.resourceReader;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class RobotsBuildingParseHandlerFunctionalTest {

    RobotsConfig config = new RobotsConfig();
    RobotsFactory factory = new RobotsFactory(config);
    ExpressionCompiler agentExpressionCompiler = factory.createAgentExpressionCompiler();
    ExpressionCompiler pathExpressionCompiler = factory.createPathExpressionCompiler();
    Matcher<String> ALL = pathExpressionCompiler.compile("*");

    @Test
    public void givenDailyMailBoards_whenParse_thenRobotsObjectEqualsExpected() throws IOException, ParseException {
        Reader reader = resourceReader("http_boards.dailymail.co.uk_robots.txt");
        RobotsParser robotsTxtParser = new RobotsParser(reader);
        RobotsBuildingParseHandler handler = new RobotsBuildingParseHandler(
                pathExpressionCompiler,
                agentExpressionCompiler);
        robotsTxtParser.parse(handler);

        Robots expected = new Robots.Builder()
                .withGroup(new Group.Builder()
                        .withDirective(new AgentDirective("*", agentExpressionCompiler.compile("*")))
                        .withDirective(new PathDirective(PathDirective.Field.disallow, "*.js", ALL))
                        .withDirective(new PathDirective(PathDirective.Field.disallow, "/search.php*", ALL))
                        .withDirective(new PathDirective(PathDirective.Field.disallow, "/includes/", ALL))
                        .withDirective(new PathDirective(PathDirective.Field.disallow, "/install/", ALL))
                        .withDirective(new PathDirective(PathDirective.Field.disallow, "/customavatars/", ALL))
                        .build())
                .build();

        Robots actual = handler.get();

        assertThat(actual, equalTo(expected));
    }

    @Test
    public void givenWwwBrandwatchCom_whenParse_thenRobotsObjectEqualsExpected() throws IOException, ParseException {
        Reader reader = resourceReader("http_www.brandwatch.com_robots.txt");
        RobotsParser robotsTxtParser = new RobotsParser(reader);
        RobotsConfig config = new RobotsConfig();
        RobotsBuildingParseHandler handler = new RobotsBuildingParseHandler(pathExpressionCompiler, agentExpressionCompiler);
        robotsTxtParser.parse(handler);

        Robots expected = new Robots.Builder()
                .withGroup(new Group.Builder()
                        .withDirective(new AgentDirective("iisbot/1.0 (+http://www.iis.net/iisbot.html)", agentExpressionCompiler.compile("iisbot/1.0 (+http://www.iis.net/iisbot.html)")))
                        .withDirective(new PathDirective(PathDirective.Field.allow, "/", ALL))
                        .build())
                .withGroup(new Group.Builder()
                        .withDirective(new AgentDirective("*", factory.createAgentExpressionCompiler().compile("*")))
                        .withDirective(new PathDirective(PathDirective.Field.disallow, "/wp-admin/", ALL))
                        .withDirective(new PathDirective(PathDirective.Field.disallow, "/wp-includes/", ALL))
                        .build())
                .withNonGroupDirective(new SiteMapDirective("http://www.brandwatch.com/sitemap.xml.gz"))
                .withNonGroupDirective(new SiteMapDirective("http://www.brandwatch.com/de/sitemap.xml.gz"))
                .build();

        Robots actual = handler.get();

        assertThat(actual, equalTo(expected));
    }


}