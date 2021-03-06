using UnityEngine;

namespace HerderGames.Schule
{
    public class Klassenraum : MonoBehaviour
    {
        [SerializeField] private string Name;
        
        public bool PlayerInside { get; private set; }
        
        private void OnTriggerEnter(Collider other)
        {
            if (other.GetComponent<Player.Player>() != null)
            {
                PlayerInside = true;
            }
        }

        private void OnTriggerExit(Collider other)
        {
            if (other.GetComponent<Player.Player>() != null)
            {
                PlayerInside = false;
            }
        }

        public string GetName()
        {
            return Name;
        }
    }
}