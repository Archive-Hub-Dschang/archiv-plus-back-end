import numpy as np
import pandas as pd
from scipy.stats import f

def anova_two_way_sans_replication():
    result = []
    nom_facteur_A = input("Nom du facteur A (par lignes, ex : Méthode) : ")
    nom_facteur_B = input("Nom du facteur B (par colonnes, ex : Niveau) : ")

    alpha = input("Entrez votre seuil de signification (par défaut 0.05) : ")
    if alpha == "":
        alpha = 0.05
    else:
        alpha = float(alpha)
    r = int(input(f"Nombre de ligne du {nom_facteur_A} : "))
    c = int(input(f"Nombre de colonne du {nom_facteur_B} : "))
    if r == 1 and c == 1:
        result.append("Impossible de réaliser une ANOVA avec un seul groupe (r=1, c=1).")
        return "\n".join(result)
    elif r == 1:
        result.append(f"Un seul niveau pour {nom_facteur_A} détecté. Réalisation d'une ANOVA à un facteur {nom_facteur_B} uniquement.")
        return "\n".join(result)
    elif c == 1:
        result.append(f"Un seul niveau pour {nom_facteur_B} détecté. Réalisation d'une ANOVA à un facteur {nom_facteur_A} uniquement.")
        return "\n".join(result)
    else:
        result.append(f"Entrez les données pour chaque combinaison {nom_facteur_A} × {nom_facteur_B} (1 valeur par combinaison) :")
        data = np.zeros((r, c))
        for i in range(r):
            for j in range(c):
                val = float(input(f"Valeur pour A={i+1}, B={j+1} : "))
                data[i, j] = val

        sum_R = np.sum(data, axis=1)
        sum_C = np.sum(data, axis=0)
        somme_carres = np.sum(data**2)
        somme_totale = np.sum(data)
        N = r * c
        # Somme des carrés
        SST = somme_carres - (somme_totale ** 2) / N
        SSR = 1/c * np.sum((sum_R)**2) - (somme_totale ** 2) / N
        SSC = 1/r * np.sum((sum_C )**2)- (somme_totale ** 2) / N
        SSE = SST - SSR - SSC
        # degré de liberté 
        dfc = c - 1
        dfr = r - 1
        dfe = (c - 1) * (r - 1)
        dft = r * c - 1
        # carres des sommes
        MSC = SSC / dfc
        MSR = SSR / dfr
        MSE = SSE / dfe
        # valeur du test
        FC = MSC / MSE
        FR = MSR / MSE
        tableau = pd.DataFrame({
            "Source de variation": [f"Effet de {nom_facteur_B}", f"Effet de {nom_facteur_A}", "Erreur", "Totale"],
            "Somme des Carrés": [SSC, SSR, SSE, SST],
            "ddl": [dfc, dfr, dfe, dft],
            "Carré des sommes": [MSC, MSR, MSE, ''],
            "F": [FC, FR, '', '']
        })
        result.append("\n\nTABLEAU DE L'ANOVA (SANS RÉPLICATION)\n")
        result.append(tableau.to_string(index=False))

        Fcritc = f.ppf(1 - alpha, dfc, dfe)
        Fcritr = f.ppf(1 - alpha, dfr, dfe)
        result.append("point critique et zone de non rejet de H0 ,H0'")
        result.append(f"\nValeurs critiques pour alpha = {alpha} :")
        result.append(f"F critique pour {nom_facteur_A} : {Fcritr:.4f}")
        result.append(f"F critique pour {nom_facteur_B} : {Fcritc:.4f}")
        if FC > 0 and FC < Fcritc:
            result.append(f"On accepte H0 {nom_facteur_B} : il n'y a pas un effet significatif du facteur {nom_facteur_B}.")
        else:
            result.append(f"On accepte H1 {nom_facteur_A} : il y a un effet significatif du facteur {nom_facteur_A}.")
        if FR > 0 and FR < Fcritr:
            result.append(f"On accepte H0 {nom_facteur_A} : il n'y a pas un effet significatif du facteur {nom_facteur_A}.")
        else:
            result.append(f"On accepte H1' {nom_facteur_B} : il y a un effet significatif du facteur {nom_facteur_B}.")

    return "\n".join(result)

if __name__ == "__main__":
    print(anova_two_way_sans_replication())