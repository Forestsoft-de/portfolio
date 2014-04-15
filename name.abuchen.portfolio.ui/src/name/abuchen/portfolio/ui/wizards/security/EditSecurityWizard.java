package name.abuchen.portfolio.ui.wizards.security;

import name.abuchen.portfolio.model.Client;
import name.abuchen.portfolio.model.Security;
import name.abuchen.portfolio.ui.Messages;
import name.abuchen.portfolio.ui.wizards.AbstractWizardPage;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

public class EditSecurityWizard extends Wizard
{
    private final Security security;

    private final EditSecurityModel model;

    public EditSecurityWizard(Client client, Security security)
    {
        this.security = security;

        this.model = new EditSecurityModel(client, security);

        this.setNeedsProgressMonitor(true);
    }

    @Override
    public void addPages()
    {
        addPage(new SecurityMasterDataPage(model));
        addPage(new AttributesPage(model));
        addPage(new SecurityTaxonomyPage(model));
        addPage(new QuoteProviderPage(model));
        addPage(new SearchSecurityWizardPage(model));

        AbstractWizardPage.attachPageListenerTo(this.getContainer());
    }

    @Override
    public boolean performFinish()
    {
        ((AbstractWizardPage) this.getContainer().getCurrentPage()).afterPage();

        boolean hasQuotes = !security.getPrices().isEmpty();
        boolean providerChanged = (model.getFeed() != null ? !model.getFeed().equals(security.getFeed()) : security
                        .getFeed() != null)
                        || (model.getTickerSymbol() != null ? !model.getTickerSymbol().equals(
                                        security.getTickerSymbol()) : security.getTickerSymbol() != null);

        model.applyChanges();

        if (hasQuotes && providerChanged)
        {
            MessageDialog dialog = new MessageDialog(getShell(), //
                            Messages.MessageDialogProviderChanged, null, //
                            Messages.MessageDialogProviderChangedText, //
                            MessageDialog.QUESTION, //
                            new String[] { Messages.MessageDialogProviderAnswerKeep,
                                            Messages.MessageDialogProviderAnswerReplace }, 0);
            if (dialog.open() == 1)
                security.removeAllPrices();
        }

        return true;
    }

}
