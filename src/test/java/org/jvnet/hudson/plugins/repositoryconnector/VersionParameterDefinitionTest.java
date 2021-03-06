package org.jvnet.hudson.plugins.repositoryconnector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.jvnet.hudson.plugins.repositoryconnector.aether.Aether;
import org.mockito.Mock;

import hudson.model.Item;

public class VersionParameterDefinitionTest extends AbstractArtifactTest {

    @Mock
    private Item mockItem;

    @Test
    public void testGetVersions() throws Exception {
        when(mockAether.resolveAvailableVersions(eq(null), any(), eq(false), any())).thenReturn(Arrays.asList("2"));
        when(mockAether.resolveAvailableVersions(eq(null), any(), eq(true), any())).thenReturn(Arrays.asList("1"));

        VersionParameterDefinition definition = createVersionParameterDefinition();
        definition.setUseLatest(false);
        definition.setUseRelease(false);

        List<String> versions = definition.getVersions();
        assertEquals(1, versions.size());
        assertEquals("2", versions.get(0));

        definition.setUseLatest(true);
        definition.setUseRelease(true);

        versions = definition.getVersions();

        assertEquals(3, versions.size());
        assertEquals("RELEASE", versions.get(0));
        assertEquals("LATEST", versions.get(1));
        assertEquals("2", versions.get(2));

        // the jelly uses the 'inverse' of the checkbox, so we need to simulate that here
        definition.setOldestFirst(false);
        versions = definition.getVersions();

        assertEquals(3, versions.size());
        assertEquals("1", versions.get(0));
        assertEquals("RELEASE", versions.get(1));
        assertEquals("LATEST", versions.get(2));
    }

    @Test
    public void testGetVersionParameterValue() {
        VersionParameterDefinition definition = createVersionParameterDefinition();
        definition.setRepositoryId("repositoryId");

        VersionParameterValue value = (VersionParameterValue) definition.createValue("version");

        assertEquals("name", value.getName());
        assertEquals("version", value.getValue());
        assertEquals("desc", value.getDescription());

        assertEquals("groupId", value.getGroupId());
        assertEquals("artifactId", value.getArtifactId());
        assertEquals("repositoryId", value.getRepositoryId());
    }

    @Test
    public void testEscapeInputs() {
        VersionParameterDefinition vpd = new VersionParameterDefinition("<", "<", "", "");

        assertEquals("&lt;", vpd.getName());
        assertEquals("&lt;", vpd.getDescription());

        assertNull(vpd.getGroupId());
        assertNull(vpd.getArtifactId());
    }

    @SuppressWarnings("serial")
    private VersionParameterDefinition createVersionParameterDefinition() {
        return new VersionParameterDefinition("name", "desc", "groupId", "artifactId") {
            @Override
            Aether createAether() {
                return mockAether;
            }

            @Override
            Item getProject() {
                return mockItem;
            }
        };
    }
}
