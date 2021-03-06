package ru.vyarus.gradle.plugin.python

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder

/**
 * @author Vyacheslav Rusakov
 * @since 28.08.2018
 */
class AbsoluteVirtualenvLocationKitTest extends AbstractKitTest {

    @Rule
    final TemporaryFolder envDir = new TemporaryFolder()

    def "Check virtualenv configuration with absolute path"() {

        setup:
        build """    
            plugins {
                id 'ru.vyarus.use-python'
            }
                        
            python {
                envPath = "${envDir.root.canonicalPath.replace('\\', '\\\\')}" 

                pip 'click:6.7'
            }
                                                                                
            task sample(type: PythonTask) {
                command = '-c print(\\'samplee\\')'
            }                        

        """

        when: "run task"
        BuildResult result = run(':sample')

        then: "task successful"
        result.task(':sample').outcome == TaskOutcome.SUCCESS
        result.output =~ /click\s+6.7/
        result.output.contains('samplee')

        then: "virtualenv created at correct path"
        result.output.contains("${envDir.root.canonicalPath}${File.separator}")
    }
}
