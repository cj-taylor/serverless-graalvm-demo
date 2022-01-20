package software.amazonaws.example.infrastructure;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.assertions.Template;

@ExtendWith(SnapshotExtension.class)
public class InfrastructureStackSnapshotTest {

    private Expect expect;

    private static Template template;

    @BeforeAll
    static void setup() {
        var app = new App();

        var stack = new InfrastructureStack(app, "test", StackProps.builder().build());

        template = Template.fromStack(stack);
    }

    @Test
    public void testSnapshot() throws IOException {
        expect.toMatchSnapshot(template.toJSON());
    }
}
