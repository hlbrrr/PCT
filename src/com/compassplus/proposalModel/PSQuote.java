package com.compassplus.proposalModel;

import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: arudin
 * Date: 3/30/13
 * Time: 11:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class PSQuote {
    private Proposal proposal;

    public PSQuote(Proposal proposal) {
        this.proposal = proposal;
    }

    public PSQuote(Node psQuote, Proposal proposal) {
        this.proposal = proposal;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
/*        sb.append("<Service>");
        sb.append("<Key>").append(this.getKey()).append("</Key>");
        sb.append("</Service>");*/
        return sb.toString();
    }
}
