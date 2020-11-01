package com.dabsquared.gitlabjenkins.connection;


import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.domains.URIRequirementBuilder;
import com.dabsquared.gitlabjenkins.gitlab.api.GitLabClient;
import hudson.Extension;
import hudson.model.Item;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.model.Run;
import hudson.security.ACL;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;

import org.antlr.v4.runtime.misc.NotNull;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.DoNotUse;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.interceptor.RequirePOST;

/**
 * @author Robin Müller
 */
public class GitLabConnectionProperty extends JobProperty<Job<?, ?>> {

    private String gitLabConnection;
    private String jobCredentialId;
	private boolean useAlternativeCredential = false;

    @DataBoundConstructor
    public GitLabConnectionProperty(String gitLabConnection) {
        this.gitLabConnection = gitLabConnection;
    }

    public String getGitLabConnection() {
        return gitLabConnection;
    }

    public String getJobCredentialId() {
        return jobCredentialId;
    }

    public boolean isUseAlternativeCredential() {
        return useAlternativeCredential;
    }

    @DataBoundSetter
    public void setJobCredentialId(String jobCredentialId) {
        this.jobCredentialId = jobCredentialId;
    }

    @DataBoundSetter
    public void setUseAlternativeCredential(boolean useAlternativeCredential) {
        this.useAlternativeCredential = useAlternativeCredential;
    }

    public GitLabClient getClient() {
        if (StringUtils.isNotEmpty(gitLabConnection)) {
            GitLabConnectionConfig connectionConfig = (GitLabConnectionConfig) Jenkins.getActiveInstance().getDescriptor(GitLabConnectionConfig.class);
            return connectionConfig != null ? connectionConfig.getClient(gitLabConnection, this.owner, jobCredentialId)
                   : null;
        }
        return null;
    }

    public static GitLabClient getClient(@NotNull Run<?, ?> build) {
        Job<?, ?> job = build.getParent();
        if(job != null) {
            final GitLabConnectionProperty connectionProperty = job.getProperty(GitLabConnectionProperty.class);
            if (connectionProperty != null) {
                return connectionProperty.getClient();
            }
        }
        
        return null;
    }

    @Extension
    @Symbol("gitLabConnection")
    public static class DescriptorImpl extends JobPropertyDescriptor {

        @Override
        public String getDisplayName() {
            return "GitLab connection";
        }

        @Override
        public boolean isApplicable(Class<? extends Job> jobType) {
            return true;
        }

        @Override
        public JobProperty<?> newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            return req.bindJSON(GitLabConnectionProperty.class, formData);
        }

        public ListBoxModel doFillGitLabConnectionItems() {
            ListBoxModel options = new ListBoxModel();
            GitLabConnectionConfig descriptor = (GitLabConnectionConfig) Jenkins.getInstance().getDescriptor(GitLabConnectionConfig.class);
            for (GitLabConnection connection : descriptor.getConnections()) {
                options.add(connection.getName(), connection.getName());
            }
            return options;
        }
        
        public ListBoxModel doFillJobCredentialIdItems(@AncestorInPath Item item, @QueryParameter String url,
               @QueryParameter String jobCredentialId) {
            StandardListBoxModel result = new StandardListBoxModel();
            return result.includeEmptyValue()
                    .includeMatchingAs(ACL.SYSTEM, item, StandardCredentials.class,
                            URIRequirementBuilder.fromUri(url).build(), new GitLabCredentialMatcher())
                    .includeCurrentValue(jobCredentialId);
        }

        @RequirePOST
        @Restricted(DoNotUse.class)
        public FormValidation doTestConnection(@QueryParameter String jobCredentialId,
                @QueryParameter String gitLabConnection, @AncestorInPath Item item) {
        	Jenkins.getActiveInstance().checkPermission(Jenkins.ADMINISTER);
             try {
                GitLabConnection gitLabConnectionTested = null;
                GitLabConnectionConfig descriptor = (GitLabConnectionConfig) Jenkins.getInstance()
                        .getDescriptor(GitLabConnectionConfig.class);
                for (GitLabConnection connection : descriptor.getConnections()) {
                    if (gitLabConnection.equals(connection.getName())) {
                         gitLabConnectionTested = connection;
                    }
                }
                if (gitLabConnectionTested == null) {
                    return FormValidation.error(Messages.connection_error("The GitLab Connection does not exist"));
                }
                new GitLabConnection("", gitLabConnectionTested.getUrl(), jobCredentialId,
                        gitLabConnectionTested.getClientBuilderId(), true,
                        gitLabConnectionTested.getConnectionTimeout(), gitLabConnectionTested.getReadTimeout())
                                .getClient(item, jobCredentialId).getCurrentUser();
                return FormValidation.ok(Messages.connection_success());
            } catch (WebApplicationException e) {
                return FormValidation.error(Messages.connection_error(e.getMessage()));
            } catch (ProcessingException e) {
                return FormValidation.error(Messages.connection_error(e.getCause().getMessage()));
            }
        }
    }
}
