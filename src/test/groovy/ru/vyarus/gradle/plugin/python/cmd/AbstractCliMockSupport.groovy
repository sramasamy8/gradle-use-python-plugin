package ru.vyarus.gradle.plugin.python.cmd

import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.Project
import org.gradle.initialization.DefaultBuildCancellationToken
import org.gradle.internal.file.PathToFileResolver
import org.gradle.process.internal.DefaultExecAction
import org.gradle.util.ConfigureUtil
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import ru.vyarus.gradle.plugin.python.util.ExecRes
import ru.vyarus.gradle.plugin.python.util.TestLogger
import spock.lang.Specification

import java.util.concurrent.Executors

/**
 * @author Vyacheslav Rusakov
 * @since 20.11.2017
 */
abstract class AbstractCliMockSupport extends Specification {

    // used to overcome manual file existence check on win
    @Rule
    TemporaryFolder dir

    Project project
    TestLogger logger

    boolean isWin = Os.isFamily(Os.FAMILY_WINDOWS)

    File file(String path) {
        new File(dir.root, path)
    }

    void setup() {
        project = Stub(Project)
        logger = new TestLogger()
        project.getLogger()  >>  { logger }
        project.getProjectDir() >> { dir.root }
        project.file(_) >> { new File(dir.root, it[0]) }
    }

    void mockExec(Project project, String output, int res) {
        // check execution with logs without actual execution
        project.exec(_) >> {Closure spec ->
            DefaultExecAction act = ConfigureUtil.configure(spec,
                    new DefaultExecAction(Stub(PathToFileResolver),
                            Executors.newSingleThreadExecutor(),
                            new DefaultBuildCancellationToken()))
            OutputStream os = act.standardOutput
            if (output) {
                os.write(output.bytes)
            }
            return new ExecRes(res)
        }
    }
}
