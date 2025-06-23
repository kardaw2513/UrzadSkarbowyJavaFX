package Services;

import Model.Mandat;
import Model.Wizyta;
import Model.Wniosek;

import java.util.List;
import java.util.stream.Collectors;

public class RaportService {
    private final MandatService mandatService;
    private final WniosekService wniosekService;
    private final WizytaService wizytaService;

    public RaportService(MandatService ms, WniosekService ws, WizytaService vs) {
        this.mandatService = ms;
        this.wniosekService = ws;
        this.wizytaService = vs;
    }

    public List<Mandat> raportMandatow(boolean tylkoOplacone) {
        return mandatService.wszystkieMandaty().stream()
                .filter(m -> !tylkoOplacone || m.isOplacony())
                .collect(Collectors.toList());
    }

    public List<Wizyta> raportWizyt() {
        return wizytaService.wszystkieWizyty();
    }

    public List<Wniosek> raportWnioskow(Wniosek.Status status) {
        return wniosekService.wszystkieWnioski().stream()
                .filter(w -> w.getStatus() == status)
                .collect(Collectors.toList());
    }
}