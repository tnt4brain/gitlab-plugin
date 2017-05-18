package com.dabsquared.gitlabjenkins.gitlab.hook.model;


import net.karneim.pojobuilder.GeneratePojoBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Nikolay Ustinov
 */
@GeneratePojoBuilder(intoPackage = "*.builder.generated", withFactoryMethod = "*")
public class NoteHook extends WebHook {

    private User user;
    private Project project;
    private MergeRequestObjectAttributes mergeRequest;
    private NoteObjectAttributes objectAttributes;
    private Commit commit;

    public Commit getCommit() {
        return commit;
    }

    public User getUser() {
        return user;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public NoteObjectAttributes getObjectAttributes() {
        return objectAttributes;
    }

    public void setObjectAttributes(NoteObjectAttributes objectAttributes) {
        this.objectAttributes = objectAttributes;
    }

    public MergeRequestObjectAttributes getMergeRequest() {
        return mergeRequest;
    }

    public void setMergeRequest(MergeRequestObjectAttributes mergeRequest) {
        this.mergeRequest = mergeRequest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NoteHook that = (NoteHook) o;
        return new EqualsBuilder()
                .append(user, that.user)
                .append(project, that.project)
                .append(objectAttributes, that.objectAttributes)
                .append(mergeRequest, that.mergeRequest)
                .append(commit, that.commit)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(user)
                .append(project)
                .append(objectAttributes)
                .append(mergeRequest)
                .append(commit)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("user", user)
                .append("project", project)
                .append("objectAttributes", objectAttributes)
                .append("mergeRequest", mergeRequest)
                .append("commit", commit)
                .toString();
    }
}
